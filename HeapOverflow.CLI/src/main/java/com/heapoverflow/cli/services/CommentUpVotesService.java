package com.heapoverflow.cli.services;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.ApiEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;

public class CommentUpVotesService {

        public static JsonNode deleteCommentUpVote(int commentId) throws Exception {
                return HttpUtils
                                .syncDelete((EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                                                + ApiEndpointsConstants.API_COMMENTS_UPVOTES) + "/" + commentId);
        }

        public static JsonNode postCommentUpVote(int commentId) throws Exception {
                return HttpUtils
                                .syncPost(
                                                EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                                                                + ApiEndpointsConstants.API_COMMENTS_UPVOTES,
                                                Map.of(

                                                                "commentId", commentId));
        }

}
