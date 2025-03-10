package com.heapoverflow.api.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "threads")
public class Thread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "thread_id")
    private Integer id;

    @Column(name = "thread_title", nullable = false)
    private String title;

    @Column(name = "thread_description", nullable = false)
    private String description;

    @Column(name = "user_google_id", nullable = false)
    private String userGoogleId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

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

    public String getUserGoogleId() {
        return userGoogleId;
    }

    public void setUserGoogleId(String userGoogleId) {
        this.userGoogleId = userGoogleId;
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
                ", userGoogleId='" + userGoogleId + '\'' +
                ", createdAt=" + createdAt +
                ", closedAt=" + closedAt +
                '}';
    }
}
