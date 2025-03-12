package com.heapoverflow.api.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "thread_upvotes")
public class ThreadUpvote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "thread_upvote_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_google_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "thread_id", referencedColumnName = "thread_id", nullable = false)
    private Thread thread;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    @Override
    public String toString() {
        return "ThreadUpvote{" +
                "id=" + id +
                ", thread =" + thread +
                '}';
    }
}
