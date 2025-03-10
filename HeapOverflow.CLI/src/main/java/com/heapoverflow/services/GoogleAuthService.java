package com.heapoverflow.services;

import com.heapoverflow.constants.EnvConstants;
import com.heapoverflow.utils.EnvUtils;
import com.heapoverflow.utils.SystemUtils;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GoogleAuthService {
    public static CompletableFuture<Boolean> getUsersIdToken() {
        CompletableFuture<Boolean> futureToken = new CompletableFuture<>();
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

                    // send code to backend

                    String response = "Authentication successful! You can close this tab.";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();

                    server.stop(1);
                    futureToken.complete(true); // Complete the future successfully
                } catch (Exception e) {
                    futureToken.completeExceptionally(e);
                }
            });

            server.start();

            // Schedule timeout to stop the server after 1 minute
            scheduler.schedule(() -> {
                server.stop(1);
                futureToken.complete(false); // Return false to signal auth failed
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
}
