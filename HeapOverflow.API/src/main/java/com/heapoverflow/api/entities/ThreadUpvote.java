package com.heapoverflow.api.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "thread_upvotes")
public class ThreadUpvote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "thread_upvote_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_google_id", referencedColumnName = "user_google_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "thread_id", referencedColumnName = "thread_id", nullable = false)
    private Thread thread;

    public ThreadUpvote() {}

    public ThreadUpvote(User user, Thread thread) {
        this.user = user;
        this.thread = thread;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

}
