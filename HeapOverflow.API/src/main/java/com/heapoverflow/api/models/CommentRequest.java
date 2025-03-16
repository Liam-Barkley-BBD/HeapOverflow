package com.heapoverflow.api.models;

public class CommentRequest {
    
    private String content;
    private Integer threadId;

    public CommentRequest() {}

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getThreadId() {
        return threadId;
    }

    public void setThreadId(Integer threadId) {
        this.threadId = threadId;
    };

}
