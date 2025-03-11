package com.heapoverflow.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class Reply {
    @JsonProperty("reply_id")
    private Integer id;

    @JsonProperty("user_google_id")
    private String userGoogleId;

    @JsonProperty("comment_id")
    private Integer commentId;

    @JsonProperty("content")
    private String content;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public Reply() {}

    public Reply(Integer id, String userGoogleId, Integer commentId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.userGoogleId = userGoogleId;
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUserGoogleId() { return userGoogleId; }
    public void setUserGoogleId(String userGoogleId) { this.userGoogleId = userGoogleId; }

    public Integer getCommentId() { return commentId; }
    public void setCommentId(Integer commentId) { this.commentId = commentId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Reply{" +
                "id=" + id +
                ", userGoogleId='" + userGoogleId + '\'' +
                ", commentId=" + commentId +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
