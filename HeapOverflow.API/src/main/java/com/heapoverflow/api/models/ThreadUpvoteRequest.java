package com.heapoverflow.api.models;

public class ThreadUpvoteRequest {
    
    private String userId;
    private Integer threadId;

    public ThreadUpvoteRequest() {}

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
