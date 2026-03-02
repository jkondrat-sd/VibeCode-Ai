package com.vibecode.project.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProjectVersionSummary {

    private Integer versionNumber;
    private String message;
    private LocalDateTime createdAt;

}
