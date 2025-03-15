package com.heapoverflow.cli.models;

public class ReplyRequest {
    
    private String content;
    private String userId;
    private Integer commentId;

    public ReplyRequest() {}

    public ReplyRequest(String content, String userId, Integer commentId) {
        this.content = content;
        this.userId = userId;
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getCommentdId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    };

}
