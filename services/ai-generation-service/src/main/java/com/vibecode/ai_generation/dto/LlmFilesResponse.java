package com.vibecode.ai_generation.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LlmFilesResponse {
    private List<GeneratedFileDto> files;
}
