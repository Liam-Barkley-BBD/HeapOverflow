package com.heapoverflow.api.models;

import java.time.LocalDateTime;

public class ThreadUpdate {

    private String title;
    private String description;
    private LocalDateTime closedAt;

    public ThreadUpdate() {}

    public String getTitle() {
        return title;
    }
    
    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
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
}
