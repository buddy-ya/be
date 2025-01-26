package com.team.buddyya.chatting.repository;

import com.team.buddyya.chatting.domain.ChatRequest;
import com.team.buddyya.student.domain.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRequestRepository extends JpaRepository<ChatRequest, Long> {

    List<ChatRequest> findAllByReceiver(Student student);
}
