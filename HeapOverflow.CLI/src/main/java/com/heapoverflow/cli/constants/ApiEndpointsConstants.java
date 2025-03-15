package com.heapoverflow.cli.constants;

public class ApiEndpointsConstants {
    // user-controller
    public static final String API_USERS = "users";
    public static final String API_USERS_GID = "users/";
    public static final String API_USERS_USERNAME_USERNAME = "users/usernames/";
    public static final String API_USERS_EMAIL_EMAIL = "users/email/";

    // reply-controller
    public static final String API_REPLIES = "replies";
    public static final String API_REPLIES_ID = "replies/";
    public static final String API_REPLIES_COMMENT_COMMENT_ID = "replies/comments/";
    public static final String API_REPLIES_USER_GID = "replies/user/";

    // comment-controller
    public static final String API_COMMENTS = "comments";
    public static final String API_COMMENT_ID = "comments/";
    public static final String API_COMMENTS_USER_GID = "comments/user/";
    public static final String API_COMMENTS_THREAD_THREAD_ID = "comments/thread/";
}
