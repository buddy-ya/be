package com.team.buddyya.chatting.domain;

import com.team.buddyya.student.domain.Student;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private Chatroom chatroom;

    @Column(name = "unread_count")
    private int unreadCount;

    @Column(name = "leave_time")
    private LocalDateTime leaveTime;

    @Builder
    public ChatroomStudent(Student student, Chatroom chatroom) {
        this.student = student;
        this.chatroom = chatroom;
        this.unreadCount = 0;
        this.leaveTime = LocalDateTime.now();
    }

    public void increaseUnreadCount() {
        this.unreadCount++;
    }

    public void resetUnreadCount() {
        this.unreadCount = 0;
    }

    public void updateLeaveTime() {
        this.leaveTime = LocalDateTime.now();
    }


}

