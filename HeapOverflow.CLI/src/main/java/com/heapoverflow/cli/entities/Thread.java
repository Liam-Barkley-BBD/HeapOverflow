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

    @JsonProperty("user")
    private User user;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("closed_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime closedAt;

    public Thread() {
    }

    public Thread(Integer id, String title, String description, User user, LocalDateTime createdAt,
            LocalDateTime closedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.user = user;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    @Override
    public String toString() {
        return "Thread{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", user=" + (user != null ? user.getUsername() : "Unknown") + '\'' +
                ", createdAt=" + createdAt +
                ", closedAt=" + closedAt +
                '}';
    }
}
