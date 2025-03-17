package com.heapoverflow.cli.constants;

public class ApiEndpointsConstants {
    // user-controller
    public static final String API_USERS = "api/users";
    public static final String API_USERS_GID = "api/users/";
    public static final String API_USERS_USERNAME_USERNAME = "api/users/usernames/";
    public static final String API_USERS_EMAIL_EMAIL = "api/users/email/";

    // reply-controller
    public static final String API_REPLIES = "api/replies";
    public static final String API_REPLIES_ID = "api/replies/";
    public static final String API_REPLIES_COMMENT_COMMENT_ID = "api/replies/comments/";
    public static final String API_REPLIES_USER_GID = "api/replies/user/";

    // comment-controller
    public static final String API_COMMENTS = "api/comments";
    public static final String API_COMMENT_ID = "api/comments/";
    public static final String API_COMMENTS_USER_GID = "api/comments/user/";
    public static final String API_COMMENTS_THREAD_THREAD_ID = "api/comments/thread/";

    // threads-controller
    public static final String API_THREADS = "api/threads";
    public static final String API_THREADS_ID = "api/threads/";

    // Upvote
    public static final String API_COMMENTS_UPVOTES = "api/commentupvotes";
    public static final String API_THREADS_UPVOTES = "api/threadupvotes";
}
