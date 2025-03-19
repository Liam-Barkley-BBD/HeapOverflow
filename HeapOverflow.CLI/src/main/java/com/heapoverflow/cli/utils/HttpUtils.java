package com.heapoverflow.cli.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heapoverflow.cli.constants.EnvConstants;

public class HttpUtils {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode syncGet(String url) throws Exception {
        String token = "";
        try {
            token = EnvUtils.retrieveValue(EnvConstants.JWT_TOKEN);
        } catch (Exception error) {
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

    public static JsonNode syncPost(String url, Object requestBody) throws Exception {
        return sendJsonRequest(url, requestBody, "POST");
    }

    public static JsonNode syncPut(String url, Object requestBody) throws Exception {
        return sendJsonRequest(url, requestBody, "PUT");
    }

    public static JsonNode syncPatch(String url, Object requestBody) throws Exception {
        return sendJsonRequest(url, requestBody, "PATCH");
    }

    public static JsonNode syncDelete(String url) throws Exception {
        String token = "";
        try {
            token = EnvUtils.retrieveValue(EnvConstants.JWT_TOKEN);
        } catch (Exception error) {
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

    private static JsonNode sendJsonRequest(String url, Object requestBody, String method) throws Exception {
        try {
            String token = "";
            try {
                token = EnvUtils.retrieveValue(EnvConstants.JWT_TOKEN);
            } catch (Exception error) {
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
            throw new RuntimeException("Request failed: " + e.getMessage(), e);
        }
    }

    private static JsonNode sendRequest(HttpRequest request) throws Exception {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();

        if (statusCode == 401) {
            try {
                EnvUtils.deleteKeys();
            } catch (Exception error) {
                throw new RuntimeException("Your token could not be reset due to" + error.getMessage() + ", please try signing out manually and signing in again.");
            }
            throw new RuntimeException("Your session has expired, please type the login command to login again");
        } else if (statusCode == 404) {
            throw new RuntimeException("Resource not found");
        } else if (statusCode >= 400) {
            throw new RuntimeException(statusCode + " " + response.body());
        } else {
            try {
                return objectMapper.readTree(response.body());
            } catch (Exception error) {
                throw new RuntimeException("Could not parse JSON response body", error);
            }
        }
    }
}
