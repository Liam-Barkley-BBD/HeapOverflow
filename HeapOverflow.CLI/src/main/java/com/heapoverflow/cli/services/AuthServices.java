package com.heapoverflow.cli.services;
import java.util.concurrent.CompletableFuture;

public class AuthServices {
    public static CompletableFuture<String> attemptGoogleLogin() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String authCode = BrowserAuthServices.getUsersGoogleAuthCode().join();

                if(authCode.equals("")){
                    return "Authentication took too long or failed, releasing resources";
                } else{
                    return "Authentication successful, welcome to HeapOverflow.CLI";
                }
            } catch (Exception e) {
                throw new RuntimeException("Error ecountered in attempting google login: " + e.getMessage(), e);
            }
        });
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
