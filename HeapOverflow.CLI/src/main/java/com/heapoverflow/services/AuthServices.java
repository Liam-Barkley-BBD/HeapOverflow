package com.heapoverflow.services;
import java.util.concurrent.CompletableFuture;

public class AuthServices {
    public static void attemptGoogleLogin() {
        String authCode = BrowserAuthServices.getUsersGoogleAuthCode().join();

        if(authCode.equals("")){
            System.out.println("Authentication took too long or failed, releasing resources");
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
