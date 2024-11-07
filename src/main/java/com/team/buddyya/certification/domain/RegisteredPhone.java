package com.team.buddyya.certification.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.*;

@Getter
@Table(name = "registered_phone_number")
@NoArgsConstructor(access = PROTECTED)
@Entity
public class RegisteredPhone {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 11, nullable = false, unique = true)
    private String phoneNumber;

    @Column(length = 6, nullable = false, unique = true)
    private String authenticationCode;

    public RegisteredPhone(String phoneNumber, String authenticationCode) {
        this.phoneNumber = phoneNumber;
        this.authenticationCode = authenticationCode;
    }

    public void setAuthenticationCode(String authenticationCode) {
        this.authenticationCode = authenticationCode;
    }
}