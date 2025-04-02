package com.team.buddyya.student.dto.response;

import com.team.buddyya.certification.domain.RegisteredPhone;

public record InvitationCodeResponse(String code, Boolean participated) {

    public static InvitationCodeResponse from(RegisteredPhone registeredPhone) {
        return new InvitationCodeResponse(registeredPhone.getInvitationCode(),registeredPhone.getInvitationEventParticipated());
    }
}
