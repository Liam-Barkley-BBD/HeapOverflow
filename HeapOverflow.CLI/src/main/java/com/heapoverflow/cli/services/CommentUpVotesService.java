package com.heapoverflow.cli.services;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.ApiEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;

public class CommentUpVotesService {

        public static JsonNode deleteCommentUpVote(String userId, int id) throws Exception {
                return HttpUtils
                                .asyncDelete((EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                                                + ApiEndpointsConstants.API_COMMENTS_UPVOTES) + id)
                                .join();
        }

        public static JsonNode postCommentUpVote(String userId, String commentId) throws Exception {
                return HttpUtils
                                .asyncPost(
                                                EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                                                                + ApiEndpointsConstants.API_REPLIES,
                                                Map.of(
                                                                "userId", userId,
                                                                "commentId", commentId))
                                .join();
        }

}
