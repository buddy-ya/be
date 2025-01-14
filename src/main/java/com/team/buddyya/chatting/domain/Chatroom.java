package com.team.buddyya.chatting.domain;

import com.team.buddyya.common.domain.CreatedTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "chatroom")
public class Chatroom extends CreatedTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    private String name;

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatroomStudent> chatroomStudents;

    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chats;

    public Chatroom() {
        this.chatroomStudents = new ArrayList<>();
        this.chats = new ArrayList<>();
    }

    @Builder
    public Chatroom(Long postId, String name) {
        this();
        this.postId = postId;
        this.name = name;
    }
}