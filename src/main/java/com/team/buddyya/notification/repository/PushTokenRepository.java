package com.team.buddyya.notification.repository;

import com.team.buddyya.notification.domain.PushToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {
    Optional<PushToken> findByUserId(String userId);
}