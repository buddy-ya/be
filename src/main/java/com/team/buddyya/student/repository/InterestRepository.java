package com.team.buddyya.student.repository;

import com.team.buddyya.student.domain.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {

    Optional<Interest> findByInterestName(String interestName);
}
