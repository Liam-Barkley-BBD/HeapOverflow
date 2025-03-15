package com.heapoverflow.cli.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heapoverflow.cli.constants.EnvConstants;

import java.util.Map;

public class HttpUtils {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static CompletableFuture<SafeMap> asyncGet(String url) {
        String token = "";
        try{
            token = EnvUtils.getStringEnvOrThrow(EnvConstants.JWT);
        }catch(Exception error){
            // we would rather return a bad request from the server
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Authorization", "Bearer " + token)
                .build();

        return sendRequest(request);
    }

    public static CompletableFuture<SafeMap> asyncPost(String url, Object requestBody) {
        return sendJsonRequest(url, requestBody, "POST");
    }

    public static CompletableFuture<SafeMap> asyncPut(String url, Object requestBody) {
        return sendJsonRequest(url, requestBody, "PUT");
    }

    public static CompletableFuture<SafeMap> asyncPatch(String url, Object requestBody) {
        return sendJsonRequest(url, requestBody, "PATCH");
    }

    public static CompletableFuture<SafeMap> asyncDelete(String url) {
        String token = "";
        try{
            token = EnvUtils.getStringEnvOrThrow(EnvConstants.JWT);
        }catch(Exception error){
            // we would rather return a bad request from the server
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .header("Authorization", "Bearer " + token)
                .build();

        return sendRequest(request);
    }

    private static CompletableFuture<SafeMap> sendJsonRequest(String url, Object requestBody, String method) {
        try {
            String token = "";
            try{
                token = EnvUtils.getStringEnvOrThrow(EnvConstants.JWT);
            }catch(Exception error){
                // we would rather return a bad request from the server
            }

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method(method, HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();

            return sendRequest(request);
        } catch (Exception e) {
            CompletableFuture<SafeMap> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }

    private static CompletableFuture<SafeMap> sendRequest(HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                int statusCode = response.statusCode();

                if (statusCode >= 400) {
                    throw new RuntimeException(statusCode + " " + response.body());
                }

                try {
                    Map<String, Object> map = objectMapper.readValue(response.body(), new TypeReference<>() {});
                    return new SafeMap(map);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse JSON response", e);
                }
            });
    }
}
