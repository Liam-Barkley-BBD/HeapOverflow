package com.heapoverflow.cli.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpUtils {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static CompletableFuture<JsonNode> asyncGet(String url) throws Exception{
        String token = "";
        try{
            token = EnvUtils.retrieveJwt();
        }catch(Exception error){
            // we would rather return a bad request from the server
        }

        HttpRequest request = switch (token) {
            case "" -> HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();
            default -> HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .header("Authorization", "Bearer " + token)
                        .build();
        };

        return sendRequest(request);
    }

    public static CompletableFuture<JsonNode> asyncPost(String url, Object requestBody) {
        return sendJsonRequest(url, requestBody, "POST");
    }

    public static CompletableFuture<JsonNode> asyncPut(String url, Object requestBody) {
        return sendJsonRequest(url, requestBody, "PUT");
    }

    public static CompletableFuture<JsonNode> asyncPatch(String url, Object requestBody) {
        return sendJsonRequest(url, requestBody, "PATCH");
    }

    public static CompletableFuture<JsonNode> asyncDelete(String url) throws Exception{
        String token = "";
        try{
            token = EnvUtils.retrieveJwt();
        }catch(Exception error){
            // we would rather return a bad request from the server
        }

        HttpRequest request = switch (token) {
            case "" -> HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .DELETE()
                        .build();
            default -> HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .DELETE()
                        .header("Authorization", "Bearer " + token)
                        .build();
        };

        return sendRequest(request);
    }

    private static CompletableFuture<JsonNode> sendJsonRequest(String url, Object requestBody, String method) {
        try {
            String token = "";
            try{
                token = EnvUtils.retrieveJwt();
            }catch(Exception error){
                // we would rather return a bad request from the server
            }

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = switch (token) {
                case "" -> HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .method(method, HttpRequest.BodyPublishers.ofString(jsonBody))
                            .header("Content-Type", "application/json")
                            .build();
                default -> HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .method(method, HttpRequest.BodyPublishers.ofString(jsonBody))
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + token)
                            .build();
            };

            return sendRequest(request);
        } catch (Exception e) {
            CompletableFuture<JsonNode> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }

    private static CompletableFuture<JsonNode> sendRequest(HttpRequest request) throws Exception {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                System.out.println(response.body());
                int statusCode = response.statusCode();

                if (statusCode >= 400) {
                    throw new RuntimeException(statusCode + " " + response.body());
                }

                try{
                    return objectMapper.readTree(response.body());
                } catch(Exception error){
                    throw new RuntimeException("Could not parse JSON response body");
                }
            });
    }
}
