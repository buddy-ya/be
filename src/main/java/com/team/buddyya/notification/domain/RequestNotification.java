package com.team.buddyya.notification.domain;

import java.util.Map;

public class RequestNotification {
    private String to;
    private String title;
    private String body;
    private Map<String, Object> data;

    public RequestNotification(String to, String title, String body, Map<String, Object> data) {
        this.to = to;
        this.title = title;
        this.body = body;
        this.data = data;
    }
}