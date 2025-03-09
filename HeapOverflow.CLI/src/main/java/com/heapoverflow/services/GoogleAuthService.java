package com.heapoverflow.services;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.heapoverflow.constants.EnvConstants;
import com.heapoverflow.utils.EnvUtils;
import com.heapoverflow.utils.SystemUtils;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GoogleAuthService {
    public static CompletableFuture<String> getUsersIdToken() {
        CompletableFuture<String> futureToken = new CompletableFuture<>();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(EnvUtils.getIntEnvOrThrow(EnvConstants.LOCAL_AUTH_PORT)), 0);
            server.createContext("/callback", exchange -> {
                try {
                    String query = exchange.getRequestURI().getQuery();
                    if (query == null || !query.contains("code=")) {
                        futureToken.completeExceptionally(new Exception("Authorization failed"));
                        return;
                    }
                    
                    String code = query.split("code=")[1].split("&")[0];

                    String response = "Authentication successful! You can close this tab.";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();

                    server.stop(1);

                    // Exchange authorization code for token
                    String idToken = getIdToken(code);
                    futureToken.complete(idToken); // Complete the future successfully
                } catch (Exception e) {
                    futureToken.completeExceptionally(e);
                }
            });

            server.start();

            // Schedule timeout to stop the server after 1 minute
            scheduler.schedule(() -> {
                server.stop(1);
                futureToken.complete(""); // Return an empty string if no token was collected
            }, EnvUtils.getIntEnvOrThrow(EnvConstants.LOCAL_AUTH_TIMEOUT), TimeUnit.SECONDS);

            // open browser for login
            String loginUrl = EnvUtils.getStringEnvOrThrow(EnvConstants.AUTH_URL) + "?client_id=" + EnvUtils.getStringEnvOrThrow(EnvConstants.CLIENT_ID) +
            "&redirect_uri=" + EnvUtils.getStringEnvOrThrow(EnvConstants.REDIRECT_URI) +
            "&response_type=code" +
            "&scope=openid%20email%20profile";

            SystemUtils.openBrowser(loginUrl);
            System.out.println("Waiting for authentication...");
        } catch (Exception e) {
            futureToken.completeExceptionally(e);
        }

        return futureToken;
    }

    private static String getIdToken(String code) throws IOException {
        AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                new NetHttpTransport(),
                new GsonFactory(),
                new GenericUrl(EnvUtils.getStringEnvOrThrow(EnvConstants.TOKEN_URL)),
                new ClientParametersAuthentication(
                    EnvUtils.getStringEnvOrThrow(EnvConstants.CLIENT_ID), 
                    EnvUtils.getStringEnvOrThrow(EnvConstants.CLIENT_SECRET)),
                EnvUtils.getStringEnvOrThrow(EnvConstants.CLIENT_ID),
                EnvUtils.getStringEnvOrThrow(EnvConstants.AUTH_URL)
        ).setScopes(Collections.singletonList("openid email profile")) // Include 'openid' scope
        .build();

        // request token
        TokenResponse tokenResponse = flow.newTokenRequest(code)
                .setRedirectUri(EnvUtils.getStringEnvOrThrow(EnvConstants.REDIRECT_URI))
                .setClientAuthentication(new ClientParametersAuthentication(
                    EnvUtils.getStringEnvOrThrow(EnvConstants.CLIENT_ID), 
                    EnvUtils.getStringEnvOrThrow(EnvConstants.CLIENT_SECRET)))
                .execute();

        String idToken = tokenResponse.get("id_token").toString(); // id token is a valid jwt token
        return idToken;
    }
}
