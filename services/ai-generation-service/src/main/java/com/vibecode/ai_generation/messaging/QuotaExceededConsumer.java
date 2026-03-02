package com.vibecode.ai_generation.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.vibecode.ai_generation.cache.QuotaBlockRegistry;
import com.vibecode.ai_generation.event.UserQuotaExceededEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuotaExceededConsumer {

    private final QuotaBlockRegistry blockRegistry;

    @KafkaListener(topics = "user.quota.exceeded", groupId = "ai-group")
    public void consume(UserQuotaExceededEvent event) {

        log.warn("Blocking user due to quota exceeded: {}", event.getUserId());

        blockRegistry.blockUser(event.getUserId());
    }
}