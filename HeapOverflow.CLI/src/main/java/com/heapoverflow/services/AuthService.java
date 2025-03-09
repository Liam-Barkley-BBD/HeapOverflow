package com.heapoverflow.services;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.util.concurrent.CompletableFuture;

import com.heapoverflow.auth.GoogleAuth;

public class AuthService {
    public static CompletableFuture<Boolean> attemptGoogleLogin() {
        CompletableFuture<Boolean> loginStatusFuture = new CompletableFuture<>();
        CompletableFuture<String> usersIdTokenFuture = GoogleAuth.getUsersIdToken();

        usersIdTokenFuture.thenAccept(token -> {
            System.out.println("ID Token: " + token);
            // Use the token for API requests
            loginStatusFuture.complete(true);
        }).exceptionally(ex -> {
            System.err.println("Authentication failed: " + ex.getMessage());
            loginStatusFuture.complete(false);
            return null;
        });

        return loginStatusFuture;
    }

    public static CompletableFuture<Boolean> isLoggedIn() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
                return true;
            } catch (Exception e) {
                throw new RuntimeException("Error calling Google API: " + e.getMessage(), e);
            }
        });
    }
}
