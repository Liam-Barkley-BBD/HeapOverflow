package com.heapoverflow.cli.services;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.ApiEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;

public class ThreadUpvoteServices {
        public static JsonNode deleteThreadUpVote(String id) throws Exception {

                return HttpUtils
                                .syncDelete((EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                                                + ApiEndpointsConstants.API_THREADS_UPVOTES) + "?threadId=" + id);
        }

        public static JsonNode postThreadUpVote(int threadId) throws Exception {
                return

                HttpUtils
                                .syncPost(
                                                EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                                                                + ApiEndpointsConstants.API_THREADS_UPVOTES,

                                                Map.of("threadId", threadId));
        }

}
