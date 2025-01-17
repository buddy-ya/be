package com.team.buddyya.chatting.repository;

import com.team.buddyya.chatting.domain.ChatroomStudent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatroomStudentRepository extends JpaRepository<ChatroomStudent, Long> {

    boolean existsByStudentIdAndChatroomId(Long studentId, Long chatroomId);
}
