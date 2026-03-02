package com.vibecode.billing.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="usage_logs",
    uniqueConstraints= @UniqueConstraint(
        columnNames="event_id"
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsageLog {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="event_id", nullable=false, unique=true)
    private String eventId;

    @Column(name="user_id", nullable=false)
    private UUID userId;

    @Column(name="project_id")
    private UUID projectId;

     @Column(name = "tokens_used", nullable = false)
    private Integer tokensUsed;

    @Column(nullable=false)
    private String model;

    @Column(name="cost_usd")
    private BigDecimal costUsd;

    @Column(name="duration_ms")
    private Integer durationMs;

    @Column(nullable=false)
    private Instant timestamp;

    @Column(name="processed_at")
    private Instant processedAt;
}