package com.vibecode.billing.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.vibecode.billing.Repository.UsageLogRepository;
import com.vibecode.billing.client.UserServiceClient;
import com.vibecode.billing.event.UserQuotaExceededEvent;
import com.vibecode.billing.dto.UserUsageResponse;
import com.vibecode.billing.entity.UsageLog;
import com.vibecode.billing.event.GenerationCompleteEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenerationCompleteConsumer {

    private final BillingEventProducer billingEventProducer;
    private final UsageLogRepository usageLogRepository;
    private final UserServiceClient userServiceClient;



    @KafkaListener(
        topics="generation.complete",
        groupId="billing-group"
    )
    public void consume(GenerationCompleteEvent event){
        log.info("Received generation event: {}", event.getEventId());
        
        if(usageLogRepository.existsByEventId(event.getEventId())){
            log.warn("Duplicate event detected: {}", event.getEventId());
            return;
        }

        UsageLog usageLog = UsageLog.builder()
            .eventId(event.getEventId())
            .userId(event.getUserId())
            .projectId(event.getProjectId())
            .tokensUsed(event.getTokensUsed())
            .model(event.getModel())
            .durationMs(event.getDurationMs())
            .timestamp(event.getTimestamp())
            .processedAt(java.time.Instant.now())
            .build();
        
        usageLogRepository.save(usageLog);

        //increment usage in user
        userServiceClient.incrementUsage(event.getUserId(), event.getTokensUsed());

        //fetch updated useage
        UserUsageResponse usage = userServiceClient.getUsage(event.getUserId());

        //check if quota exceeded
        if(usage.getRemainingTokens() <=0){
            log.info("User {} has exceeded their quota", event.getUserId());
            //publish quota exceeded event
            billingEventProducer.publishQuotaExceeded(
                UserQuotaExceededEvent.builder()
                    .userId(event.getUserId())
                    .timestamp(java.time.Instant.now())
                    .reason("Token quota exceeded")
                    .build()
                
            );
        }

        log.info("Saved usage log for event: {}", event.getEventId());
    
    }
    
}
