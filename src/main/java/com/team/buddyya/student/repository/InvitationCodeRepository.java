package com.team.buddyya.student.repository;

import com.team.buddyya.certification.domain.RegisteredPhone;
import com.team.buddyya.student.domain.InvitationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationCodeRepository extends JpaRepository<InvitationCode, Long> {

    Optional<InvitationCode> findByStudentId(long studentId);

    Optional<InvitationCode> findByCode(String code);

    List<InvitationCode> findAllByRegisteredPhone(RegisteredPhone registeredPhone);
}
