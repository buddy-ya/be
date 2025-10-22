package com.team.buddyya.student.repository;

import com.team.buddyya.feed.dto.projection.FeedAuthorInfo;
import com.team.buddyya.student.domain.Student;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByPhoneNumber(String phoneNumber);

    Optional<Object> findByEmail(String email);

    @Query("SELECT new com.team.buddyya.feed.dto.projection.FeedAuthorInfo(" +
            "s.id, s.name, s.country, s.role, s.characterProfileImage, s.isCertificated, s.isDeleted, u.universityName) "
            +
            "FROM Student s JOIN s.university u WHERE s.id IN :studentIds")
    List<FeedAuthorInfo> findAuthorInfoByIdsIn(@Param("studentIds") Set<Long> studentIds);
}
