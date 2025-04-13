package com.team.buddyya.notification.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class RequestNotification {

    private String to;
    private String title;
    private String body;
    private String priority;
    private String channelId;
    private String sound;
    private Map<String, Object> data;

    @Builder
    public RequestNotification(String to, String title, String body, Map<String, Object> data) {
        this.to = to;
        this.title = title;
        this.body = body;
        this.data = data;
        this.priority = "high";
        this.sound = "default";
        this.channelId = ("default");
    }
}