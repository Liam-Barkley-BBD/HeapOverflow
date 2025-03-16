package com.heapoverflow.api.models;

public class ReplyRequest {
    
    private String content;
    private Integer commentId;

    public ReplyRequest() {}

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    };

}
