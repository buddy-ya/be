package com.team.buddyya.notification.dto.request;

public record PushToAllUsersRequest(long feedId, String title, String body) {
}
