package com.team.buddyya.student.repository;

import com.team.buddyya.student.domain.ProfileImage;
import com.team.buddyya.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage> findByStudent(Student student);
}

