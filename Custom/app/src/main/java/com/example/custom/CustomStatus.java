package com.example.custom;

public class CustomStatus {
    private String statusMessage;
    private String statusTime;

    public CustomStatus(String statusMessage, String statusTime) {
        this.statusMessage = statusMessage;
        this.statusTime = statusTime;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusTime() {
        return this.statusTime;
    }

    public void setStatusTime(String statusTime) {
        this.statusTime = statusTime;
    }
}
