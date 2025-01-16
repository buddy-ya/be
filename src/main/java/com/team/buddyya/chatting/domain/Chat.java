package com.team.buddyya.chatting.domain;

import com.team.buddyya.common.domain.CreatedTime;
import com.team.buddyya.student.domain.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(name = "chat")
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