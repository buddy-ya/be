package com.team.buddyya.certification.domain;

import com.team.buddyya.common.domain.CreatedTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.*;

@Entity
@Table(name = "registered_phone_number")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class RegisteredPhone extends CreatedTime {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 11, nullable = false, unique = true)
    private String phoneNumber;

    @Column(length = 6, nullable = false, unique = true)
    private String authenticationCode;

    @Column(name = "deleted", nullable = false)
    private Boolean isDeleted;

    public RegisteredPhone(String phoneNumber, String authenticationCode, boolean isDeleted) {
        this.phoneNumber = phoneNumber;
        this.authenticationCode = authenticationCode;
        this.isDeleted = isDeleted;
    }

    public void updateAuthenticationCode(String authenticationCode) {
        this.authenticationCode = authenticationCode;
    }

    public void updateIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
