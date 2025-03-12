package com.heapoverflow.cli.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpUtils {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> CompletableFuture<T> asyncGet(String url, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return sendRequest(request, responseType);
    }

    public static <T> CompletableFuture<T> asyncPost(String url, Object requestBody, Class<T> responseType) {
        return sendJsonRequest(url, requestBody, responseType, "POST");
    }

    public static <T> CompletableFuture<T> asyncPut(String url, Object requestBody, Class<T> responseType) {
        return sendJsonRequest(url, requestBody, responseType, "PUT");
    }

    public static <T> CompletableFuture<T> asyncPatch(String url, Object requestBody, Class<T> responseType) {
        return sendJsonRequest(url, requestBody, responseType, "PATCH");
    }

    public static <T> CompletableFuture<T> asyncDelete(String url, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();

        return sendRequest(request, responseType);
    }

    private static <T> CompletableFuture<T> sendJsonRequest(String url, Object requestBody, Class<T> responseType, String method) {
        try {
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method(method, HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json")
                    .build();

            return sendRequest(request, responseType);
        } catch (Exception e) {
            CompletableFuture<T> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }

    private static <T> CompletableFuture<T> sendRequest(HttpRequest request, Class<T> responseType) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    try {
                        return objectMapper.readValue(response.body(), responseType);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse JSON response", e);
                    }
                });
    }
}
