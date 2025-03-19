package com.heapoverflow.cli.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

        public static JsonNode getThreads(String search, int page, int size)

                        throws Exception {

                StringBuilder apiUrl = new StringBuilder(
                                EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                                                + ApiEndpointsConstants.API_THREADS + "?page=" + page + "&size="
                                                + size);
                if (search != null && !search.isEmpty()) {

                        apiUrl.append("&searchText=" + URLEncoder.encode(search, StandardCharsets.UTF_8.toString()));
                } else {
                        apiUrl.append("");
                }

                System.out.println(apiUrl.toString());
                return HttpUtils.syncGet(apiUrl.toString());
        }

        public static JsonNode getThreadsTrending(int page, int size) throws Exception {
                String url = EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                                + ApiEndpointsConstants.API_THREADS_TRENDING + "?page=" + page +
                                "&size=" + size;

                return HttpUtils.syncGet(url);
        }

        public static JsonNode getThreadsByUser(int page, int size) throws Exception {
                String url = EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                                + ApiEndpointsConstants.API_THREADS_USER + "?page=" + page +
                                "&size=" + size;

                return HttpUtils.syncGet(url);
        }

        public static JsonNode postThread(String title, String description) throws Exception {

                return HttpUtils.syncPost(
                                EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                                                + ApiEndpointsConstants.API_THREADS,
                                Map.of(
                                                "title", title,
                                                "description", description));

        }

        public static JsonNode getThreadsById(String id) throws Exception {

                return HttpUtils.syncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                                + ApiEndpointsConstants.API_THREADS_ID + id);

        }

        public static JsonNode patchThread(String id, String title, String description, Boolean closeThread)
                        throws Exception {

                Map<String, Object> fields = Stream.of(
                                Map.entry("title", title),
                                Map.entry("description", description))
                                .filter(entry -> entry.getValue() != null && !entry.getValue().isBlank())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                if (closeThread) {
                        fields.put("closedAt", LocalDateTime.now().toString());
                }

                return HttpUtils.syncPatch(
                                EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                                                + ApiEndpointsConstants.API_THREADS_ID + id,
                                fields);
        }

        public static JsonNode deleteThread(String id) throws Exception {
                return HttpUtils
                                .syncDelete(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                                                + ApiEndpointsConstants.API_THREADS_ID + id);
        }

}
