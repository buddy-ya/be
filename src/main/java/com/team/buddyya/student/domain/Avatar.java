package com.team.buddyya.student.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "avatar")
public class Avatar {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long version;

    @Column(length = 11, nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private Boolean notificationEnabled;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private Boolean loggedOut;

    private LocalDateTime deactivatedDate;

    private LocalDateTime updatedDate;
}
