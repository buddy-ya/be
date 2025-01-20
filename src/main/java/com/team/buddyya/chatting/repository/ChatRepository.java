package com.team.buddyya.chatting.repository;

import com.team.buddyya.chatting.domain.Chat;
import com.team.buddyya.chatting.domain.Chatroom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Page<Chat> findByChatroomAndCreatedDateAfter(Chatroom chatroom, LocalDateTime leaveTime, Pageable pageable);
}
