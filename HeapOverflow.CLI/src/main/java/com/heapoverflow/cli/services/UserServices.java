package com.heapoverflow.cli.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.ApiEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;

public class UserServices {
    public static JsonNode getUsers(String username, String email, int page, int size) throws Exception {
        if(!username.equals("") && !email.equals("")){
            return HttpUtils
                        .syncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_USERS +"?username=" + username + "&email=" + email + "&page=" + page +"&size=" + size);
        } else if(!username.equals("")){
            return HttpUtils
                        .syncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_USERS +"?username=" + username + "&page=" + page +"&size=" + size);
        } else if(!email.equals("")){
            return HttpUtils
                        .syncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_USERS +"?email=" + email + "&page=" + page +"&size=" + size);
        } else{
            return HttpUtils
                        .syncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_USERS + "?page=" + page +"&size=" + size);
        }
    }

    public static JsonNode getUsersByGoogleId(String userGoogleId) throws Exception {
        return HttpUtils
                .syncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_USERS_GID + userGoogleId);
    }
}
