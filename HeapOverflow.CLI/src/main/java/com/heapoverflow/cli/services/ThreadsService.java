package com.heapoverflow.cli.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.ApiEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.models.ThreadRequest;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;

public class ThreadsService {
    public static JsonNode getThreads(String title, String description, int page, int size) throws Exception {

        String url = EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_THREADS +
                "?title=" + (title != null ? title : "") +
                "&description=" + (description != null ? description : "") +
                "&page=" + page +
                "&size=" + size;
        return HttpUtils.asyncGet(url).join();
    }

    public static JsonNode getThreadsById(int id) throws Exception {

        return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                + ApiEndpointsConstants.API_THREADS_ID + id).join();

    }

    public static String postThread(String title, String description, String userId) {
        try {
            return HttpUtils.asyncPost(
                    EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_THREADS,
                    new ThreadRequest(title, description, userId)).join().toString();
        } catch (Exception error) {
            return "Error encountered getting replies from user with userGoogleId: " + error.getMessage();
        }
    }

}
