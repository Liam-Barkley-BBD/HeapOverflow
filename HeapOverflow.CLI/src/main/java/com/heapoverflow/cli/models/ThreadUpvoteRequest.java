package com.heapoverflow.cli.models;

public class ThreadUpvoteRequest {
    
    private String userId;
    private Integer threadId;

    public ThreadUpvoteRequest() {}

    public ThreadUpvoteRequest(String userId, Integer threadId) {
        this.userId = userId;
        this.threadId = threadId;
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
