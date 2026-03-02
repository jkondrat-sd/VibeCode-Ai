package com.vibecode.billing.client;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vibecode.billing.dto.UserUsageResponse;

@FeignClient(name="user-service")
public interface UserServiceClient {
   
    @PostMapping("/internal/users/{userId}/usage/increment")
    void incrementUsage(
        @PathVariable UUID userId,
        @RequestParam int tokens
    );

    @GetMapping("/users/{userId}/subscription")
    UserUsageResponse getUsage(
        @PathVariable UUID userId
    );

}