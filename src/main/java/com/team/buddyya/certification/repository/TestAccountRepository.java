package com.team.buddyya.certification.repository;

import com.team.buddyya.certification.domain.TestAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestAccountRepository extends JpaRepository<TestAccount, Long> {

    Optional<TestAccount> findByPhoneNumber(String phoneNumber);
}
