package com.vibecode.ai_generation.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibecode.ai_generation.cache.QuotaBlockRegistry;
import com.vibecode.ai_generation.clients.ProjectServiceClient;
import com.vibecode.ai_generation.clients.UserServiceClient;
import com.vibecode.ai_generation.dto.GenerateCodeRequest;
import com.vibecode.ai_generation.dto.GenerateCodeResponse;
import com.vibecode.ai_generation.dto.LlmFilesResponse;
import com.vibecode.ai_generation.dto.ProjectFile;
import com.vibecode.ai_generation.dto.UpdateProjectFilesRequest;
import com.vibecode.ai_generation.dto.UserUsageResponse;
import com.vibecode.ai_generation.integration.OllamaClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiGenerationService {

    private final ProjectServiceClient projectClient;
    private final UserServiceClient userClient;
    private final OllamaClient ollamaClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final QuotaBlockRegistry quotaBlockRegistry;

    public GenerateCodeResponse generate(
            GenerateCodeRequest request,
            String authHeader,
            UUID userId) {
        // quota check
        if (quotaBlockRegistry.isBlocked(userId)) {
            throw new RuntimeException("Quota exceeded. Upgrade your plan.");
        }

        // STEP 1 — check project access
        projectClient.getProjectFiles(request.getProjectId(), authHeader);

        // STEP 2 — check quota
        UserUsageResponse usage
                = userClient.getUsage(userId, authHeader);

        int requiredTokens = estimateTokens(request.getPrompt());

        if (usage.getRemainingTokens() < requiredTokens) {
            throw new RuntimeException("Quota exceeded");
        }

        // STEP 3 — generate mock files
        // ProjectFile generatedFile = mockGenerate(request.getPrompt());
        // String llmOutput = ollamaClient.generateCode(request.getPrompt());
        // String structuredPrompt = buildStructuredPrompt(request.getPrompt());
        // String llmOutput = ollamaClient.generateCode(structuredPrompt);
        // ProjectFile generatedFile = convertToProjectFile(llmOutput);
        // // STEP 4 — update project
        // projectClient.updateProjectFiles(
        //         request.getProjectId(),
        //         buildUpdateRequest(generatedFile, request),
        //         authHeader
        // );
        // // STEP 5 — deduct tokens
        // userClient.incrementUsage(userId, requiredTokens, authHeader);
        // return GenerateCodeResponse.builder()
        //         .projectId(request.getProjectId())
        //         .success(true)
        //         .message("AI generation completed. Tokens used: " + requiredTokens)
        //         .build();
        String structuredPrompt = buildStructuredPrompt(request.getPrompt());
        String llmOutput = ollamaClient.generateCode(structuredPrompt);

        List<ProjectFile> generatedFiles;
        try {
            generatedFiles = parseLlmResponse(llmOutput);

        } catch (Exception e) {
            // Retry once with stronger instruction
            String retryPrompt = structuredPrompt
                    + "\n\nIMPORTANT: Return ONLY valid JSON.";

            String retryOutput = ollamaClient.generateCode(retryPrompt);

            generatedFiles = parseLlmResponse(retryOutput);

        }
        UpdateProjectFilesRequest updateRequest
                = UpdateProjectFilesRequest.builder()
                        .files(generatedFiles)
                        .versionMessage("AI generation: " + request.getPrompt())
                        .build();
        projectClient.updateProjectFiles(
                request.getProjectId(),
                updateRequest,
                authHeader);
        // STEP 5 — deduct tokens
        userClient.incrementUsage(userId, requiredTokens, authHeader);
        return GenerateCodeResponse.builder()
                .projectId(request.getProjectId())
                .success(true)
                .message("AI generation completed. Tokens used: " + requiredTokens)
                .build();
    }

    private ProjectFile convertToProjectFile(String llmOutput) {

        LocalDateTime now = LocalDateTime.now();

        return ProjectFile.builder()
                .path("src/AiGenerated.js")
                .filename("AiGenerated.js")
                .language("javascript")
                .content(llmOutput)
                .size((long) llmOutput.length())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private int estimateTokens(String prompt) {
        return Math.max(50, prompt.length() / 4);
    }

    private ProjectFile mockGenerate(String prompt) {

        LocalDateTime now = LocalDateTime.now();

        String generatedContent = """
        // ======================================
        // AI GENERATED FILE
        // ======================================
        // Prompt:
        // %s
        // ======================================

        export default function GeneratedComponent() {
            return (
                <div>
                    <h1>Generated from AI prompt</h1>
                    <p>%s</p>
                </div>
            );
        }
        """.formatted(prompt, prompt);

        return ProjectFile.builder()
                .path("src/GeneratedComponent.js")
                .filename("GeneratedComponent.js")
                .language("javascript")
                .content(generatedContent)
                .size((long) generatedContent.length())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private UpdateProjectFilesRequest buildUpdateRequest(
            ProjectFile file,
            GenerateCodeRequest request) {

        return UpdateProjectFilesRequest.builder()
                .files(List.of(file))
                .versionMessage("AI generation: " + request.getPrompt())
                .build();
    }

    public String generateTest(String prompt) {
        return ollamaClient.generateCode(prompt);
    }

    private String buildStructuredPrompt(String userPrompt) {
        return """
You are an AI software generator.

You MUST return ONLY valid JSON.
No explanation.
No markdown.
No comments outside JSON.

JSON format must be:

{
  "files": [
    {
      "path": "file path with folders",
      "language": "programming language",
      "content": "full file content"
    }
  ]
}

Rules:
- Always return at least one file
- Use realistic project structure
- Content must be complete code
- Escape quotes properly
- Do not wrap JSON in ``` blocks

User request:
%s
""".formatted(userPrompt);
    }

    private String sanitizeJson(String rawOutput) {

        int firstBrace = rawOutput.indexOf("{");
        int lastBrace = rawOutput.lastIndexOf("}");

        if (firstBrace >= 0 && lastBrace > firstBrace) {
            return rawOutput.substring(firstBrace, lastBrace + 1);
        }

        throw new RuntimeException("No valid JSON found in LLM output");
    }

    private List<ProjectFile> parseLlmResponse(String llmOutput) {

        try {
            String cleanJson = sanitizeJson(llmOutput);
            LlmFilesResponse response
                    = objectMapper.readValue(cleanJson, LlmFilesResponse.class);

            LocalDateTime now = LocalDateTime.now();

            return response.getFiles().stream()
                    .map(file -> ProjectFile.builder()
                    .path(file.getPath())
                    .filename(extractFilename(file.getPath()))
                    .language(file.getLanguage())
                    .content(file.getContent())
                    .size((long) file.getContent().length())
                    .createdAt(now)
                    .updatedAt(now)
                    .build())
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse LLM JSON output", e);
        }
    }

    private String extractFilename(String path) {
        int index = path.lastIndexOf("/");
        return index >= 0 ? path.substring(index + 1) : path;
    }

}
