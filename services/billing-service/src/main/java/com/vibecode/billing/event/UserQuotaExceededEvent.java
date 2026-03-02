package com.vibecode.billing.event;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQuotaExceededEvent {

    private UUID userId;
    private Instant timestamp;
    private String reason;
}