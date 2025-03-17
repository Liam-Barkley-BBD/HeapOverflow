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

    public static JsonNode getThreads(String search, int page, int size, Boolean isTrending, Boolean userThreads)
            throws Exception {

        String url = EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_THREADS +
                "?page=" + page +
                "&size=" + size +
                (search != null && !search.isEmpty() ? "&searchText=" + search : "") +
                (userThreads != null ? "&userThreads=true" : "") +
                (isTrending != null ? "&isTrending=true" : "");
        return HttpUtils.syncGet(url);
    }

    public static JsonNode postThread(String title, String description) throws Exception {

        return HttpUtils.syncPost(
                EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_THREADS,
                Map.of(
                        "title", title,
                        "description", description));

    }

    public static JsonNode getThreadsById(int id) throws Exception {

        return HttpUtils.syncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                + ApiEndpointsConstants.API_THREADS_ID + id);

    }

    public static JsonNode patchThread(int id, String title, String description, Boolean closeThread) throws Exception {

        Map<String, Object> fields = Stream.of(
                Map.entry("title", title),
                Map.entry("description", description))
                .filter(entry -> entry.getValue() != null && !entry.getValue().isBlank()) // Exclude null and empty
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (closeThread) {
            fields.put("closedAt", LocalDateTime.now());
        }

        return HttpUtils.syncPatch(
                EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_THREADS_ID + id,
                fields);
    }

    public static JsonNode deleteThread(String id) throws Exception {
        return HttpUtils
                .syncDelete(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                        + ApiEndpointsConstants.API_THREADS_ID + id);
    }

}
