package com.vibecode.billing.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.vibecode.billing.event.UserQuotaExceededEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BillingEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishQuotaExceeded(UserQuotaExceededEvent event) {
        kafkaTemplate.send("user.quota.exceeded", event);
    }
}