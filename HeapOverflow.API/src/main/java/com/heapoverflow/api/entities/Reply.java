package com.heapoverflow.api.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "replies")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Integer id;

    @Column(name = "user_google_id", nullable = false)
    private String userGoogleId;

    @ManyToOne
    @JoinColumn(name = "comment_id", referencedColumnName = "comment_id", nullable = false)
    private Comment comment;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserGoogleId() {
        return userGoogleId;
    }

    public void setUserGoogleId(String userGoogleId) {
        this.userGoogleId = userGoogleId;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "id=" + id +
                ", userGoogleId='" + userGoogleId + '\'' +
                ", comment=" + comment +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
