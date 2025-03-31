package com.team.buddyya.student.domain;

import com.team.buddyya.certification.domain.RegisteredPhone;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "invitation_code")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class InvitationCode {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "participated", nullable = false)
    private Boolean participated;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "registered_phone_id", nullable = false)
    private RegisteredPhone registeredPhone;

    @Builder
    public InvitationCode(String code, Student student, RegisteredPhone registeredPhone) {
        this.code = code;
        this.participated = false;
        this.student = student;
        this.registeredPhone = registeredPhone;
    }

    public void markAsParticipated() {
        this.participated = true;
    }
}
