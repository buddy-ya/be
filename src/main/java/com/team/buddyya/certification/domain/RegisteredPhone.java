package com.team.buddyya.certification.domain;

import com.team.buddyya.common.domain.CreatedTime;
import jakarta.persistence.*;
import lombok.Builder;
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

    @Column(name = "phone_number", length = 11, nullable = false, unique = true)
    private String phoneNumber;

    @Column(name="authentication_code", length = 6, nullable = false)
    private String authenticationCode;

    @Column(name = "invitation_code", length = 6, nullable = false, unique = true)
    private String invitationCode;

    @Column(name = "invitation_event_participated", nullable = false)
    private Boolean invitationEventParticipated;

    @Column(name = "has_withdrawn", nullable = false)
    private Boolean hasWithdrawn;

    @Builder
    public RegisteredPhone(String phoneNumber, String authenticationCode, String invitationCode) {
        this.phoneNumber = phoneNumber;
        this.authenticationCode = authenticationCode;
        this.invitationCode = invitationCode;
        this.invitationEventParticipated = false;
        this.hasWithdrawn = false;
    }

    public void updateAuthenticationCode(String authenticationCode) {
        this.authenticationCode = authenticationCode;
    }

    public void markAsInvitationEventParticipated(){ this.invitationEventParticipated = true;}

    public void updateHasWithDrawn(boolean hasWithdrawn) {
        this.hasWithdrawn = hasWithdrawn;
    }
}
