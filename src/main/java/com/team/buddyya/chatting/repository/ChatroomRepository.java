package com.team.buddyya.chatting.repository;

import com.team.buddyya.chatting.domain.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    @Query("SELECT c FROM Chatroom c " +
            "JOIN c.chatroomStudents s1 ON s1.student.id = :userId " +
            "JOIN c.chatroomStudents s2 ON s2.student.id = :buddyId")
    Optional<Chatroom> findByUserAndBuddy(@Param("userId") Long userId, @Param("buddyId") Long buddyId);
}
