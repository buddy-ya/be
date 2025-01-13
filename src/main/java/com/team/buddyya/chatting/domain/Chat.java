package com.team.buddyya.chatting.domain;

import com.team.buddyya.common.domain.CreatedTime;
import com.team.buddyya.student.domain.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Chat extends CreatedTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Builder
    public Chat(Chatroom chatroom, Student student, String message) {
        this.chatroom = chatroom;
        this.student = student;
        this.message = message;
    }
}
