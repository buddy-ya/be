package com.team.buddyya.auth.domain;

import com.team.buddyya.common.domain.CreatedTime;
import com.team.buddyya.student.domain.Student;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "auth_token")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class AuthToken extends CreatedTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String refreshToken;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @Builder
    public AuthToken(String refreshToken, Student student) {
        this.refreshToken = refreshToken;
        this.student = student;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setAuthToken(Student student) {
        this.student = student;
        student.setAuthToken(this);
    }
}
