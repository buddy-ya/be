package com.team.buddyya.certification.repository;

import com.team.buddyya.certification.domain.RegisteredPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegisteredPhoneRepository extends JpaRepository<RegisteredPhone, Long> {

    Optional<RegisteredPhone> findByPhoneNumber(String phoneNumber);

    Optional<RegisteredPhone> findByInvitationCode(String invitationCode);
}
