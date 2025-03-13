package com.heapoverflow.api.models;

public class ThreadRequest {

    private String title;
    private String description;
    private String userId;

    public ThreadRequest() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ThreadRequest{" +
                "title=" + this.title +
                ", description=" + this.description +
                ", userId='" + this.userId +
                '}';
    }
}
