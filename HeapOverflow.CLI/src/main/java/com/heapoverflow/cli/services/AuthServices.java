package com.heapoverflow.cli.services;
import java.util.concurrent.CompletableFuture;

import com.heapoverflow.cli.constants.AuthEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;
import com.heapoverflow.cli.utils.SafeMap;

public class AuthServices {
    public static CompletableFuture<String> attemptGoogleLogin() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String authCode = BrowserAuthServices.getUsersGoogleAuthCode().join();

                if(authCode.equals("")){
                    return "Browser authentication took too long or failed, releasing resources";
                } else{
                    SafeMap map = HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + AuthEndpointsConstants.AUTH_TOKEN + authCode).join();
                    return "Authentication successful, welcome to HeapOverflow.CLI " + map.toString();
                }
            } catch (Exception e) {
                throw new RuntimeException("Error ecountered in attempting google login: " + e.getMessage(), e);
            }
        });
    }
}
