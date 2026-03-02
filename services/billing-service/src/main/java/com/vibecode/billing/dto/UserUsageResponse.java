package com.vibecode.billing.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUsageResponse {

    private UUID userId;
    private String planType;
    private Integer tokenQuota;
    private Integer tokensUsed;
    private Integer remainingTokens;
}