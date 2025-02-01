package com.team.buddyya.certification.repository;

import com.team.buddyya.certification.domain.StudentEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentEmailRepository extends JpaRepository<StudentEmail, Long> {

    Optional<StudentEmail> findByEmail(String email);
}
