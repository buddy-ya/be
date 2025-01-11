package com.team.buddyya.chatting.repository;

import com.team.buddyya.chatting.domain.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
}
