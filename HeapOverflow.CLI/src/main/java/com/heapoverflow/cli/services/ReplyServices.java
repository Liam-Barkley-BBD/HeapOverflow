package com.heapoverflow.cli.services;

import com.heapoverflow.cli.constants.ApiEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.models.ReplyRequest;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;

public class ReplyServices {
    public static String getReplies() {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES).join().toString();
        } catch(Exception error){
            return "Error encountered getting all replies: " + error.getMessage();
        }
    }

    public static String getRepliesById(String id) {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES_ID + "?id=" + id).join().toString();
        } catch(Exception error){
            return "Error encountered getting replies by id: " + error.getMessage();
        }
    }

    public static String getCommentsById(String commentId) {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES_COMMENT_COMMENT_ID + "?commentId=" + commentId).join().toString();
        } catch(Exception error){
            return "Error encountered getting comments associated to commentId: " + error.getMessage();
        }
    }

    public static String getRepliesFromUser(String userGoogleId) {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES_USER_GID + "?userGoogleId=" + userGoogleId).join().toString();
        } catch(Exception error){
            return "Error encountered getting replies from user with userGoogleId: " + error.getMessage();
        }
    }

    public static String postReply(String content, String userId, Integer commentId) {
        try{
            return HttpUtils.asyncPost(
                    EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES,
                    new ReplyRequest(content, userId, commentId)
                ).join().toString();
        } catch(Exception error){
            return "Error encountered getting replies from user with userGoogleId: " + error.getMessage();
        }
    }
}
