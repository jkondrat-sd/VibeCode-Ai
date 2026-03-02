package com.vibecode.billing.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    private UUID id;

    @Column(name="user_id", nullable=false)
    private UUID userId;

    @Column(name="stripe_subscription_id", nullable=false, unique=true)
    private String stripeSubscriptionId;

    @Column(name="stripe_customer_id", nullable=false)
    private String stripeCustomerId;

    @Column(name="plan_type", nullable=false)
    private String planType;

    @Column(nullable=false)
    private boolean status;

    @Column(name="current_period_start", nullable=false)
    private Instant currentPeriodStart;

    @Column(name="current_period_end", nullable=false)
    private Instant currentPeriodEnd;

    @Column(name="cancel_at_period_end", nullable=false)
    private Boolean cancelAtPeriodEnd;

    @Column(name="created_at", nullable=false)
    private Instant createdAt;

    @Column(name="updated_at", nullable=false)
    private Instant updatedAt;
}