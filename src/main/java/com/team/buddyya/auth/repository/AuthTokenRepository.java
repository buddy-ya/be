package com.team.buddyya.auth.repository;

import com.team.buddyya.auth.domain.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByStudentId(Long studentId);
}
