package com.heapoverflow.api.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer id;

    @Column(name = "user_google_id", nullable = false)
    private String userGoogleId;

    @ManyToOne
    @JoinColumn(name = "thread_id", referencedColumnName = "thread_id", nullable = false)
    private Thread thread;

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

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
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
        return "Comment{" +
                "id=" + id +
                ", userGoogleId='" + userGoogleId + '\'' +
                ", thread=" + thread +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
