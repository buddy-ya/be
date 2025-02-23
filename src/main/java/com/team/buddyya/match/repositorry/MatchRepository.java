package com.team.buddyya.match.repositorry;

import com.team.buddyya.match.domain.MatchRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<MatchRequest, Long> {

    @Query("""
                SELECT m FROM MatchRequest m 
                WHERE m.isKorean <> :isKorean 
                ORDER BY m.createdDate ASC
            """)
    List<MatchRequest> findAllMatches(boolean isKorean);
}
