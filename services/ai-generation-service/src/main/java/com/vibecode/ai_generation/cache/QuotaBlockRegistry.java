package com.vibecode.ai_generation.cache;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class QuotaBlockRegistry {

    private final Set<UUID> blockedUsers = ConcurrentHashMap.newKeySet();

    public void blockUser(UUID userId) {
        blockedUsers.add(userId);
    }

    public boolean isBlocked(UUID userId) {
        return blockedUsers.contains(userId);
    }
}