package com.vibecode.ai_generation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneratedFileDto {
    private String path;
    private String language;
    private String content;

}
