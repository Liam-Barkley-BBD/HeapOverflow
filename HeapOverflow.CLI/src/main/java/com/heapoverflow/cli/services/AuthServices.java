package com.heapoverflow.cli.services;

import com.heapoverflow.cli.constants.AuthEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;
import com.heapoverflow.cli.utils.SafeMap;

public class AuthServices {
    public static String attemptGoogleLogin() {
        try {
            String authCode = BrowserAuthServices.getUsersGoogleAuthCode().join();

            if(authCode.equals("")){
                return "Browser authentication took too long or failed, releasing resources";
            } else{
                SafeMap map = HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + AuthEndpointsConstants.AUTH_TOKEN + authCode).join();
                EnvUtils.storeJwt(map.getString("jwt"));
                return "Authentication successful, welcome to HeapOverflow.CLI!";
            }
        } catch (Exception error) {
            return "Error encountered in attempting google login: " + error.getMessage();
        }
    }
}
