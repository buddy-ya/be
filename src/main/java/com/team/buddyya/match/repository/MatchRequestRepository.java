package com.team.buddyya.match.repository;

import com.team.buddyya.match.domain.MatchRequest;
import com.team.buddyya.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {

    @Query("""
    SELECT m FROM MatchRequest m
    WHERE m.matchRequestStatus = 'MATCH_PENDING'
    ORDER BY m.isChineseAvailable DESC, m.createdDate ASC
    """)
    List<MatchRequest> findAllPendingMatchesPrioritizeChinese();

    @Query("""
        SELECT m FROM MatchRequest m 
        WHERE m.matchRequestStatus = 'MATCH_PENDING'
        ORDER BY m.createdDate ASC
    """)
    List<MatchRequest> findAllPendingMatches();

    void deleteByStudent(Student student);

    Optional<MatchRequest> findByStudentId(Long studentId);
}
