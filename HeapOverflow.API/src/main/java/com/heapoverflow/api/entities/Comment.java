package com.heapoverflow.api.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_google_id", referencedColumnName = "user_google_id", nullable = false)
    private User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "thread_id", referencedColumnName = "thread_id", nullable = false)
    private Thread thread;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "comment")
    private List<CommentUpvote> commentUpvotes;

    public Comment() {}

    public Comment(String content, User user, Thread thread) {
        this.content = content;
        this.user = user;
        this.thread = thread;
        this.createdAt = LocalDateTime.now();
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    public Thread getThread() {
        return this.thread;
    }

    public Integer getThreadId() {
        return this.thread.getId();
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    @JsonIgnore
    public User getUser() {
        return this.user;
    }
    
    public String getUserId() {
        return this.user.getId();
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonIgnore
    public List<CommentUpvote> getCommentUpvotes() {
        return this.commentUpvotes;
    }

    public Integer getCommentUpvotesCount() {
        return this.commentUpvotes.size();
    }

    public void setCommentUpvotes(List<CommentUpvote> commentUpvotes) {
        this.commentUpvotes = commentUpvotes;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + this.id +
                ", thread=" + this.thread +
                ", content='" + this.content + '\'' +
                ", createdAt=" + this.createdAt +
                '}';
    }
}
