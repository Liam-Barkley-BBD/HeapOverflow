package com.heapoverflow.api.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "comment_upvotes")
public class CommentUpvotes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_upvote_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_google_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id", referencedColumnName = "comment_id", nullable = false)
    private Comment comment;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "CommentUpvote{" +
                "id=" + id +
                ", comment =" + comment +
                '}';
    }
}
