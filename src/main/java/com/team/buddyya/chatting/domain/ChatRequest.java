package com.team.buddyya.chatting.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.team.buddyya.common.domain.BaseTime;
import com.team.buddyya.student.domain.Student;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_request")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ChatRequest extends BaseTime {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Student sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Student receiver;

    @Builder
    public ChatRequest(Student sender, Student receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }
}
