package com.heapoverflow.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comment_upvotes")
public class CommentUpvote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_upvote_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_google_id", referencedColumnName = "user_google_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id", referencedColumnName = "comment_id", nullable = false)
    private Comment comment;

    public CommentUpvote() {}

    public CommentUpvote(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    public Comment getComment() {
        return this.comment;
    }

    public Integer getCommentId() {
        return this.comment.getId();
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

}
