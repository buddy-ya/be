package com.team.buddyya.student.repository;

import com.team.buddyya.student.domain.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    Optional<Avatar> findByPhoneNumber(String phoneNumber);
}
