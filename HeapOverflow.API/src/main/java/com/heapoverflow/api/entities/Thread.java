package com.heapoverflow.api.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.Formula;

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

    @ManyToOne()
    @JoinColumn(name = "user_google_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Formula("(SELECT COUNT(*) FROM thread_upvotes tu WHERE tu.thread_id = thread_id)")
    private Integer threadUpvotesCount;

    public Thread() {}

    public Thread(String title, String description, User user) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getThreadUpvotesCount() {
        return threadUpvotesCount != null ? threadUpvotesCount : 0;
    }
    
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
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
                "id=" + this.id +
                ", title='" + this.title + '\'' +
                ", description='" + this.description + '\'' +
                ", user=" + (this.user != null ? this.user.getId() : "null") +
                ", createdAt=" + this.createdAt +
                ", closedAt=" + this.closedAt +
                '}';
    }
}
