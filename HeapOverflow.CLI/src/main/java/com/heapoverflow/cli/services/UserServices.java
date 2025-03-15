package com.heapoverflow.cli.services;

import java.util.Map;

import com.heapoverflow.cli.constants.ApiEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;
import com.heapoverflow.cli.utils.SafeMap;

public class UserServices {
    public static SafeMap getUsers() {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_USERS).join();
        } catch(Exception error){
            return new SafeMap(Map.of("error", "Error encountered getting all users: " + error.getMessage()));
        }
    }

    public static SafeMap getUsersByGoogleId(String userGoogleId) {
        try{
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_USERS_GID + "userGoogleId=" + userGoogleId).join();
        } catch(Exception error){
            return new SafeMap(Map.of("error", "Error encountered getting all users: " + error.getMessage()));
        }
    }
}
