package com.heapoverflow.services;
import java.util.concurrent.CompletableFuture;

public class AuthService {
    public static void attemptGoogleLogin() {
        Boolean authStatus = GoogleAuthService.getUsersIdToken().join();

        if(!authStatus){
            System.out.println("Authentication took too long or failed");
            return;
        } else{
            
        }
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
