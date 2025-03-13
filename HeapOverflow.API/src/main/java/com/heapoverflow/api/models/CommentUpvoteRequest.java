package com.heapoverflow.api.models;

public class CommentUpvoteRequest {
    
    private String userId;
    private Integer commentId;

    public CommentUpvoteRequest() {}

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
