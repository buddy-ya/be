package com.team.buddyya.student.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "avatar")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Avatar {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 11, nullable = false)
    private String phoneNumber;

    @Column(name="notificationEnabled", nullable = false)
    private Boolean isNotificationEnabled;

    @Column(name="active", nullable = false)
    private Boolean isActive;

    @Column(name="loggedOut", nullable = false)
    private Boolean isLoggedOut;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Builder
    public Avatar(String phoneNumber, boolean isNotificationEnabled, Student student) {
        this.phoneNumber = phoneNumber;
        this.isNotificationEnabled = isNotificationEnabled;
        this.student = student;
        this.isActive = true;
        this.isLoggedOut = false;
    }
}