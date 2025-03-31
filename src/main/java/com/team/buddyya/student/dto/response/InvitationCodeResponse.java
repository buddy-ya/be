package com.team.buddyya.student.dto.response;

import com.team.buddyya.student.domain.InvitationCode;

public record InvitationCodeResponse(String code, Boolean participated) {

    public static InvitationCodeResponse from(InvitationCode invitationCode) {
        return new InvitationCodeResponse(invitationCode.getCode(), invitationCode.getParticipated());
    }
}
