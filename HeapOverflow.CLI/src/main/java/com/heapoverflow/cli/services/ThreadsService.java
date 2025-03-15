package com.heapoverflow.cli.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.ApiEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.models.ThreadRequest;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;

public class ThreadsService {
    public static JsonNode getThreads() throws Exception {

        return HttpUtils
                .asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_THREADS)
                .join();
    }

    public static String getThreadsById(String id) {
        try {
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                    + ApiEndpointsConstants.API_THREADS_ID + "id=" + id).join().toString();
        } catch (Exception error) {
            return "Error encountered getting all threads by id: " + error.getMessage();
        }
    }

    public static String postThread(String title, String description, String userId) {
        try {
            return HttpUtils.asyncPost(
                    EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_COMMENTS,
                    new ThreadRequest(title, description, userId)).join().toString();
        } catch (Exception error) {
            return "Error encountered getting replies from user with userGoogleId: " + error.getMessage();
        }
    }

}
