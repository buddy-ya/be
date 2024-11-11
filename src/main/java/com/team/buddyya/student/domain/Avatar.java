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

    @Column(length = 15, nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private Boolean notificationEnabled;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private Boolean loggedOut;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Builder
    public Avatar(String phoneNumber, boolean notificationEnabled, Student student) {
        this.phoneNumber = phoneNumber;
        this.notificationEnabled = notificationEnabled;
        this.student = student;
        this.active = true;
        this.loggedOut = false;
    }
}