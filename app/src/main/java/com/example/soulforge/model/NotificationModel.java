package com.example.soulforge.model;

public class NotificationModel {
    String to;
    NotificationData data;

    public NotificationModel() {
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public NotificationData getData() {
        return data;
    }

    public void setData(NotificationData data) {
        this.data = data;
    }
}
