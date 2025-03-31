package com.team.buddyya.certification.domain;

import com.team.buddyya.common.domain.CreatedTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "has_withdrawn", nullable = false)
    private Boolean hasWithdrawn;

    public RegisteredPhone(String phoneNumber, String authenticationCode, boolean hasWithdrawn) {
        this.phoneNumber = phoneNumber;
        this.authenticationCode = authenticationCode;
        this.hasWithdrawn = hasWithdrawn;
    }

    public void updateAuthenticationCode(String authenticationCode) {
        this.authenticationCode = authenticationCode;
    }

    public void updateHasWithDrawn(boolean hasWithdrawn) {
        this.hasWithdrawn = hasWithdrawn;
    }
}
