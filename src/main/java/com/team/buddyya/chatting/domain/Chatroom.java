package com.team.buddyya.chatting.domain;

import com.team.buddyya.common.domain.CreatedTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "chatroom")
public class Chatroom extends CreatedTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    private String name;

    private String lastMessage;

    private LocalDateTime lastMessageTime;

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatroomStudent> chatroomStudents;

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chats;

    @Builder
    public Chatroom(Long postId, String name) {
        this.chatroomStudents = new ArrayList<>();
        this.chats = new ArrayList<>();
        lastMessage = null;
        lastMessageTime = null;
        this.postId = postId;
        this.name = name;
    }

    public void updateLastMessage(String message) {
        this.lastMessage = message;
        this.lastMessageTime = LocalDateTime.now();
    }
}