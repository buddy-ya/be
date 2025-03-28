package com.team.buddyya.certification.repository;

import com.team.buddyya.certification.domain.PhoneInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneInfoRepository extends JpaRepository<PhoneInfo, Long> {

    Optional<PhoneInfo> findPhoneInfoByUdId(String udId);
}
