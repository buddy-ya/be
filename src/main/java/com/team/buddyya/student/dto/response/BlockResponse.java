package com.team.buddyya.student.dto.response;

public record BlockResponse(String message) {
    public static BlockResponse from(String message) {
        return new BlockResponse(message);
    }
}

