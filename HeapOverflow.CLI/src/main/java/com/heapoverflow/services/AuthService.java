package com.heapoverflow.services;
import java.util.concurrent.CompletableFuture;

public class AuthService {
    public static void attemptGoogleLogin() {
        String usersIdToken = GoogleAuthService.getUsersIdToken().join();

        if(usersIdToken.equals("")){
            System.out.println("Authentication took too long, cancelling and releasing resources");
            return;
        } else{
            System.out.println("\nHere is the token\n");
            System.out.println(usersIdToken);
            System.out.println();
            // perform all the login stuff
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
