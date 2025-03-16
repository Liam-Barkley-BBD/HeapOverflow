package com.heapoverflow.api.models;

public class CommentUpvoteRequest {
    
    private Integer commentId;

    public CommentUpvoteRequest() {}

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    };

}
