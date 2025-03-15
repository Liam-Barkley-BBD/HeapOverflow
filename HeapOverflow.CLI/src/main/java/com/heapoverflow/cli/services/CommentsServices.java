package com.heapoverflow.cli.services;

import java.util.Map;

import com.heapoverflow.cli.constants.ApiEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.models.CommentRequest;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;
import com.heapoverflow.cli.utils.SafeMap;

public class CommentsServices {
    public static SafeMap getComments() {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_COMMENTS).join();
        } catch(Exception error){
            return new SafeMap(Map.of("error", "Error encountered getting all comments: " + error.getMessage()));
        }
    }

    public static SafeMap getCommentsById(String id) {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_COMMENT_ID + "id=" + id).join();
        } catch(Exception error){
            return new SafeMap(Map.of("error", "Error encountered getting comment by id: " + error.getMessage()));
        }
    }

    public static SafeMap getCommentsByThreadId(String threadId) {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_COMMENTS_THREAD_THREAD_ID + "threadId=" + threadId).join();
        } catch(Exception error){
            return new SafeMap(Map.of("error", "Error encountered getting comments by threadId: " + error.getMessage()));
        }
    }

    public static SafeMap getCommentsByGoogleUserId(String userGoogleId) {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_COMMENTS_USER_GID + "userGoogleId=" + userGoogleId).join();
        } catch(Exception error){
            return new SafeMap(Map.of("error", "Error encountered getting comment by userGoogleId: " + error.getMessage()));
        }
    }

    public static SafeMap postComment(String content, String userId, Integer threadId) {
        try{
            return HttpUtils.asyncPost(
                    EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_COMMENTS,
                    new CommentRequest(content, userId, threadId)
                ).join();
        } catch(Exception error){
            return new SafeMap(Map.of("error", "Error encountered getting replies from user with userGoogleId: " + error.getMessage()));
        }
    }

}
