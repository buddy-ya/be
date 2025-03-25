package com.team.buddyya.certification.repository;

import com.team.buddyya.certification.domain.AdminAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminAccountRepository extends JpaRepository<AdminAccount, Long> {

    Optional<AdminAccount> findByPhoneNumber(String phoneNumber);
}
