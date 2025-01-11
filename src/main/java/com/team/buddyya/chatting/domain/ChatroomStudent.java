package com.team.buddyya.chatting.domain;

import com.team.buddyya.student.domain.Student;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "chatroom_student")
public class ChatroomStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatRoom;

    @Builder
    public ChatroomStudent(Student student, Chatroom chatroom) {
        this.student = student;
        this.chatRoom = chatroom;
    }
}
