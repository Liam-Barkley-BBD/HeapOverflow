package com.heapoverflow.cli.services;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.ApiEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;

public class ReplyServices {
    public static JsonNode getReplies(int page, int size) throws Exception {
        return HttpUtils
                    .asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES + "?page=" + page +"&size=" + size)
                    .join();
    }

    public static JsonNode getReplyById(String id) throws Exception {
        return HttpUtils
                    .asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES_ID + id)
                    .join();
    }

    public static JsonNode postReply(String content, String commentId) throws Exception {
        return HttpUtils
                    .asyncPost(
                        EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES,
                        Map.of(
                            "content", content,
                            "commentId", commentId
                        )
                    ).join();
    }

    public static JsonNode patchReply(String content, String id) throws Exception {
        return HttpUtils
                    .asyncPatch(
                        EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES + "/" + id,
                        Map.of(
                            "content", content
                        )
                    ).join();
    }

    public static JsonNode deleteReply(String id) throws Exception {
        return HttpUtils
                    .asyncDelete(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_REPLIES_ID + id)
                    .join();
    }
}
