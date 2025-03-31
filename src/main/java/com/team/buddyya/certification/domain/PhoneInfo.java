package com.team.buddyya.certification.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "phone_info")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class PhoneInfo {

    private static final Integer INITIAL_AUTHENTICATION_COUNT = 0;
    private static final int AUTHENTICATION_MAX_RANGE = 10;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "udid", unique = true)
    private String udId;

    @Column(name = "send_Message_count", nullable = false)
    private int sendMessageCount;

    @Builder
    public PhoneInfo(String udId, int sendMessageCount) {
        this.udId = udId;
        this.sendMessageCount = INITIAL_AUTHENTICATION_COUNT;
    }

    public void resetMessageSendCount() {
        this.sendMessageCount = INITIAL_AUTHENTICATION_COUNT;
    }

    public void increaseMessageSendCount() {
        this.sendMessageCount++;
    }

    public boolean isMaxSendMessageCount() {
        return this.sendMessageCount > AUTHENTICATION_MAX_RANGE;
    }
}
