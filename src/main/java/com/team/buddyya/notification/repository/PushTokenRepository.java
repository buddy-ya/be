package com.team.buddyya.notification.repository;

import com.team.buddyya.notification.domain.ExpoToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PushTokenRepository extends JpaRepository<ExpoToken, Long> {
    Optional<ExpoToken> findByUserId(Long userId);
}