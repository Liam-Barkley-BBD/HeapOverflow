package com.heapoverflow.cli.models;

public class CommentUpvoteRequest {
    
    private String userId;
    private Integer commentId;

    public CommentUpvoteRequest() {}

    public CommentUpvoteRequest(String userId, Integer commentId) {
        this.userId = userId;
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    };

}
