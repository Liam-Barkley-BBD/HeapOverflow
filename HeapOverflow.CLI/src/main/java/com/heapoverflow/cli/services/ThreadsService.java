package com.heapoverflow.cli.services;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.ApiEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;

public class ThreadsService {

    public static JsonNode getThreads(String search, int page, int size, Boolean isTrending) throws Exception {

        String url = EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_THREADS +
                "?page=" + page +
                "&size=" + size +
                (search != null && !search.isEmpty() ? "&searchText=" + search : "") +
                (isTrending != null ? "&isTrending=true" : "");
        return HttpUtils.asyncGet(url).join();
    }

    public static JsonNode postThread(String title, String description) throws Exception {

        return HttpUtils.asyncPost(
                EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_THREADS,
                Map.of(
                        "title", title,
                        "description", description))
                .join();

    }

    public static JsonNode getThreadsById(int id) throws Exception {

        return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                + ApiEndpointsConstants.API_THREADS_ID + id).join();

    }

    // shell:>update-thread --threadId 28 --title "works" --description "doesnt"
    public static JsonNode patchThread(int id, String title, String description, Boolean closeThread) throws Exception {

        Map<String, Object> fields = Stream.of(
                Map.entry("title", title),
                Map.entry("description", description))
                .filter(entry -> entry.getValue() != null && !entry.getValue().isBlank()) // Exclude null and empty
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (closeThread) {
            fields.put("closedAt", LocalDateTime.now());
        }

        return HttpUtils.asyncPatch(
                EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_THREADS_ID + id,
                fields)
                .join();
    }

    public static JsonNode deleteThread(String id) throws Exception {
        return HttpUtils
                .asyncDelete(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                        + ApiEndpointsConstants.API_THREADS_ID + id)
                .join();
    }

}
