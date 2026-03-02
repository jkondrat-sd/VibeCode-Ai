package com.vibecode.billing.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vibecode.billing.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID>{

}
