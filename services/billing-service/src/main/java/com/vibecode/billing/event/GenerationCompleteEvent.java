package com.vibecode.billing.event;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerationCompleteEvent {
    
    private String eventId;
    private UUID userId;
    private UUID projectId;
    private Integer tokensUsed;
    private String model;
    private Integer durationMs;
    private Instant timestamp;
}
