package com.team.buddyya.chatting.repository;

import com.team.buddyya.chatting.domain.ChatRequest;
import com.team.buddyya.student.domain.Student;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRequestRepository extends JpaRepository<ChatRequest, Long> {

    List<ChatRequest> findAllByReceiver(Student receiver);

    boolean existsByReceiver(Student receiver);

    boolean existsBySenderAndReceiver(Student sender, Student receiver);

    List<ChatRequest> findAllByCreatedDateBefore(LocalDateTime createdDate);
    
    void deleteByCreatedDateBefore(LocalDateTime createdDate);
}
