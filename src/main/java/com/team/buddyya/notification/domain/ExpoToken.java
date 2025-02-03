package com.team.buddyya.notification.domain;

import com.team.buddyya.student.domain.Student;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "expo_token")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ExpoToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Builder
    public ExpoToken(String token, Student student) {
        this.token = token;
        this.student = student;
    }

    public void updateToken(String token) {
        this.token = token;
    }
}
