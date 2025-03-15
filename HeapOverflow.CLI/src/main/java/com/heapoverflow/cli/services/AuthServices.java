package com.heapoverflow.cli.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.AuthEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;

public class AuthServices {
    public static String attemptGoogleLogin() {
        try {
            String authCode = BrowserAuthServices.getUsersGoogleAuthCode().join();

            if(authCode.equals("")){
                return "Browser authentication took too long or failed, releasing resources";
            } else{
                JsonNode jsonNode = HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + AuthEndpointsConstants.AUTH_TOKEN + authCode).join();

                if (jsonNode.has("jwt")) {
                    EnvUtils.storeJwt(jsonNode.get("jwt").toString());
                    return "Authentication successful, welcome to HeapOverflow.CLI!";
                } else{
                    return "Authentication failed as jwt token was not found";
                }
            }
        } catch (Exception error) {
            return "Error encountered in attempting google login: " + error.getMessage();
        }
    }
}
