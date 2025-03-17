package com.heapoverflow.cli.services;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.ApiEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;

public class CommentsServices {
    public static JsonNode getComments(int page, int size) throws Exception {
        return HttpUtils.syncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_COMMENTS + "?page=" + page +"&size=" + size);
    }

    public static JsonNode getCommentById(String id) throws Exception {
        return HttpUtils.syncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_COMMENT_ID + id);
    }

    public static JsonNode postComment(String content, String threadId) throws Exception {
        return HttpUtils
                    .syncPost(
                        EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_COMMENTS,
                        Map.of(
                            "content", content,
                            "threadId", threadId
                        )
                    );
    }

    public static JsonNode patchComment(String content, String id) throws Exception {
        return HttpUtils
                    .syncPatch(
                        EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_COMMENTS + "/" + id,
                        Map.of(
                            "content", content
                        )
                    );
    }

    public static JsonNode deleteComment(String id) throws Exception {
        return HttpUtils
                    .syncDelete(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_COMMENT_ID + id);
    }
}
