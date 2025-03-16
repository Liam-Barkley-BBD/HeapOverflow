package com.heapoverflow.api.exceptions;

public class CommentUpvoteNotFoundException extends RuntimeException {
    public CommentUpvoteNotFoundException(String message) {
        super(message);
    }
}
