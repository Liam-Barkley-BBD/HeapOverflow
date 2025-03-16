package com.heapoverflow.cli.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.ApiEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.models.ReplyRequest;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;

public class ReplyServices {
    public static JsonNode getReplies(int page, int size) throws Exception {
        return HttpUtils
                    .asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES + "?page=" + page +"&size=" + size)
                    .join();
    }

    public static JsonNode getRepliesById(String id) throws Exception {
        return HttpUtils
                    .asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES_ID + "?id=" + id)
                    .join();
    }

    public static JsonNode getCommentsById(String commentId) throws Exception {
        return HttpUtils
                    .asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES_COMMENT_COMMENT_ID + "?commentId=" + commentId)
                    .join();
    }

    public static JsonNode getRepliesFromUser(String userGoogleId) throws Exception {
        return HttpUtils
                    .asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES_USER_GID + "?userGoogleId=" + userGoogleId)
                    .join();
    }

    public static JsonNode postReply(String content, String userId, Integer commentId) throws Exception {
        return HttpUtils
                    .asyncPost(
                        EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES,
                        new ReplyRequest(content, userId, commentId)
                    ).join();
    }
}
