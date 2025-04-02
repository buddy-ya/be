package com.team.buddyya.student.repository;

import com.team.buddyya.student.domain.MatchingProfile;
import com.team.buddyya.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchingProfileRepository extends JpaRepository<MatchingProfile, Long> {

    Optional<MatchingProfile> findByStudent(Student student);
}
