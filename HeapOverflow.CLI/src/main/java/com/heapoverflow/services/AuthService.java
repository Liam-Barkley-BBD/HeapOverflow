package com.heapoverflow.services;
import java.util.concurrent.CompletableFuture;

import com.heapoverflow.auth.GoogleAuth;

public class AuthService {
    public static void attemptGoogleLogin() {
        String usersIdToken = GoogleAuth.getUsersIdToken().join();

        System.out.println(usersIdToken);
        // perform all the login stuff
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
