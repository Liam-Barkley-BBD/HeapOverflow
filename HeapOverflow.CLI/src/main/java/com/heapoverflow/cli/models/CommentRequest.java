package com.heapoverflow.cli.models;

public class CommentRequest {
    
    private String content;
    private String userId;
    private Integer threadId;

    public CommentRequest() {}

    public CommentRequest(String content, String userId, Integer threadId) {
        this.content = content;
        this.userId = userId;
        this.threadId = threadId;
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

    public Integer getThreadId() {
        return threadId;
    }

    public void setThreadId(Integer threadId) {
        this.threadId = threadId;
    };

}
