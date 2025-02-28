package com.team.buddyya.match.repository;

import com.team.buddyya.match.domain.MatchRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {

    @Query("""
                SELECT m FROM MatchRequest m 
                WHERE m.isKorean <> :isKorean 
                ORDER BY m.createdDate ASC
            """)
    List<MatchRequest> findAllMatches(boolean isKorean);


    boolean existsByStudentId(Long studentId);
}
