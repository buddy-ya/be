package com.team.buddyya.chatting.domain;

import com.team.buddyya.common.domain.CreatedTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "chatroom")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Chatroom extends CreatedTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "last_message_time", nullable = false)
    private LocalDateTime lastMessageTime;

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatroomStudent> chatroomStudents;

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatroomType type;

    @Builder
    public Chatroom(LocalDateTime createdTime, ChatroomType type) {
        this.chatroomStudents = new ArrayList<>();
        this.chats = new ArrayList<>();
        this.type = type;
        lastMessage = null;
        lastMessageTime = createdTime;
    }

    public void updateLastMessage(String message) {
        this.lastMessage = message;
        this.lastMessageTime = LocalDateTime.now();
    }
}