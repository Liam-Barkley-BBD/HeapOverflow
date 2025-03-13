package com.heapoverflow.cli.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class Comment {
    @JsonProperty("comment_id")
    private Integer id;

    @JsonProperty("user_google_id")
    private String userGoogleId;

    @JsonProperty("thread_id")
    private Integer threadId;

    @JsonProperty("content")
    private String content;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public Comment() {}

    public Comment(Integer id, String userGoogleId, Integer threadId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.userGoogleId = userGoogleId;
        this.threadId = threadId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUserGoogleId() { return userGoogleId; }
    public void setUserGoogleId(String userGoogleId) { this.userGoogleId = userGoogleId; }

    public Integer getThreadId() { return threadId; }
    public void setThreadId(Integer threadId) { this.threadId = threadId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userGoogleId='" + userGoogleId + '\'' +
                ", threadId=" + threadId +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
