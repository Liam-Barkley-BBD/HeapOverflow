package com.heapoverflow.cli.models;

import java.time.LocalDateTime;

public class ThreadRequest {

    private String title;
    private String description;
    private String userId;
    private LocalDateTime closedAt;

    public ThreadRequest() {
    }

    public ThreadRequest(String title, String description, String userId, LocalDateTime closedAt) {
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.closedAt = closedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getclosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

}
