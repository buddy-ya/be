package com.team.buddyya.chatting.domain;

import com.team.buddyya.common.domain.CreatedTime;
import com.team.buddyya.student.domain.Student;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "chatroom_student")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ChatroomStudent extends CreatedTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private Chatroom chatroom;

    @Column(name = "unread_count", nullable = false)
    private int unreadCount;

    @Column(name = "exited", nullable = false)
    private Boolean isExited;

    @Builder
    public ChatroomStudent(Student student, Chatroom chatroom) {
        this.student = student;
        this.chatroom = chatroom;
        this.unreadCount = 0;
        this.isExited = false;
    }

    public void increaseUnreadCount() {
        this.unreadCount++;
    }

    public void resetUnreadCount() {
        this.unreadCount = 0;
    }

    public void updateIsExited() {
        this.isExited = true;
    }
}

