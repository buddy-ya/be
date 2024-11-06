package com.team.buddyya.certification.dto.request;

public record EmailCodeRequest(
        String email,
        String univName,
        int code
) {
}
