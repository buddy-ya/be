package com.team.buddyya.admin.dto.request;

public record BanRequest(
        int days,
        String banReason
) {

    public static BanRequest from(int days, String banReason) {
        return new BanRequest(days, banReason);
    }
}
