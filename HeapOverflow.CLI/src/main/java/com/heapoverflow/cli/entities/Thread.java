package com.heapoverflow.cli.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class Thread {
    @JsonProperty("thread_id")
    private Integer id;

    @JsonProperty("thread_title")
    private String title;

    @JsonProperty("thread_description")
    private String description;

    @JsonProperty("user_google_id")
    private String userGoogleId;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("closed_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime closedAt;

    public Thread() {}

    public Thread(Integer id, String title, String description, String userGoogleId, LocalDateTime createdAt, LocalDateTime closedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.userGoogleId = userGoogleId;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUserGoogleId() { return userGoogleId; }
    public void setUserGoogleId(String userGoogleId) { this.userGoogleId = userGoogleId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }

    @Override
    public String toString() {
        return "Thread{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", userGoogleId='" + userGoogleId + '\'' +
                ", createdAt=" + createdAt +
                ", closedAt=" + closedAt +
                '}';
    }
}
