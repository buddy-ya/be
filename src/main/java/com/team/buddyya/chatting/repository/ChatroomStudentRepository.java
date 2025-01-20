package com.team.buddyya.chatting.repository;

import com.team.buddyya.chatting.domain.Chatroom;
import com.team.buddyya.chatting.domain.ChatroomStudent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatroomStudentRepository extends JpaRepository<ChatroomStudent, Long> {

    boolean existsByStudentIdAndChatroomId(Long studentId, Long chatroomId);

    Optional<ChatroomStudent> findByChatroomAndStudentId(Chatroom chatroom, Long studentId);
}
