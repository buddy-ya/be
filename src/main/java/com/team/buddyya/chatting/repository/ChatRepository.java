package com.team.buddyya.chatting.repository;

import com.team.buddyya.chatting.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
