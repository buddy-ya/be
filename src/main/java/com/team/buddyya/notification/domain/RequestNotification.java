package com.team.buddyya.notification.domain;

public class RequestNotification {
    private String to;      // ExponentPushToken
    private String body;
    private String title;

    public RequestNotification(String to, String body) {
        this.to = to;
        this.body = body;
    }
}