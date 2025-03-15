package com.heapoverflow.cli.services;

import java.util.Map;

import com.heapoverflow.cli.constants.ApiEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.models.ReplyRequest;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;
import com.heapoverflow.cli.utils.SafeMap;

public class ReplyServices {
    public static SafeMap getReplies() {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES).join();
        } catch(Exception error){
            return new SafeMap(Map.of("error", "Error encountered getting all replies: " + error.getMessage()));
        }
    }

    public static SafeMap getRepliesById(String id) {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES_ID + id).join();
        } catch(Exception error){
            return new SafeMap(Map.of("error", "Error encountered getting replies by id: " + error.getMessage()));
        }
    }

    public static SafeMap getCommentsById(String commentId) {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES_COMMENT_COMMENT_ID + commentId).join();
        } catch(Exception error){
            return new SafeMap(Map.of("error", "Error encountered getting comments associated to commentId: " + error.getMessage()));
        }
    }

    public static SafeMap getRepliesFromUser(String userGoogleId) {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES_USER_GID + userGoogleId).join();
        } catch(Exception error){
            return new SafeMap(Map.of("error", "Error encountered getting replies from user with userGoogleId: " + error.getMessage()));
        }
    }

    public static SafeMap postReply(String content, String userId, Integer commentId) {
        try{
            return HttpUtils.asyncPost(
                    EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES,
                    new ReplyRequest(content, userId, commentId)
                ).join();
        } catch(Exception error){
            return new SafeMap(Map.of("error", "Error encountered getting replies from user with userGoogleId: " + error.getMessage()));
        }
    }
}
