package com.heapoverflow.cli.services;

import com.heapoverflow.cli.constants.ApiEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.models.CommentRequest;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;

public class CommentsServices {
    public static String getComments() {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_COMMENTS).join().toString();
        } catch(Exception error){
            return "Error encountered getting all comments: " + error.getMessage();
        }
    }

    public static String getCommentsById(String id) {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_COMMENT_ID + "id=" + id).join().toString();
        } catch(Exception error){
            return "Error encountered getting comment by id: " + error.getMessage();
        }
    }

    public static String getCommentsByThreadId(String threadId) {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_COMMENTS_THREAD_THREAD_ID + "threadId=" + threadId).join().toString();
        } catch(Exception error){
            return "Error encountered getting comments by threadId: " + error.getMessage();
        }
    }

    public static String getCommentsByGoogleUserId(String userGoogleId) {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_COMMENTS_USER_GID + "userGoogleId=" + userGoogleId).join().toString();
        } catch(Exception error){
            return "Error encountered getting comment by userGoogleId: " + error.getMessage();
        }
    }

    public static String postComment(String content, String userId, Integer threadId) {
        try{
            return HttpUtils.asyncPost(
                    EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_COMMENTS,
                    new CommentRequest(content, userId, threadId)
                ).join().toString();
        } catch(Exception error){
            return "Error encountered getting replies from user with userGoogleId: " + error.getMessage();
        }
    }

}
