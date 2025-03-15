package com.heapoverflow.cli.services;

import java.util.Map;

import com.heapoverflow.cli.constants.ApiEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.models.ThreadRequest;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;
import com.heapoverflow.cli.utils.SafeMap;

public class ThreadsSevice {
    public static SafeMap getThreads() {
        try {
            return HttpUtils
                    .asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_THREADS)
                    .join();
        } catch (Exception error) {
            return new SafeMap(Map.of("error", "Error encountered getting all threads: " + error.getMessage()));
        }
    }

    public static SafeMap getThreadsById(String id) {
        try {
            return HttpUtils.asyncGet(EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI)
                    + ApiEndpointsConstants.API_THREADS_ID + "id=" + id).join();
        } catch (Exception error) {
            return new SafeMap(Map.of("error", "Error encountered getting all threads by id: " + error.getMessage()));
        }
    }

 public static SafeMap postThread( String title, String description, String userId ){
    try{
        return HttpUtils.asyncPost(
                    EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI) + ApiEndpointsConstants.API_COMMENTS,
                    new ThreadRequest(title,description, userId )).join();
    }catch(Exception error){
        return new SafeMap(Map.of("error", "Error encountered getting replies from user with userGoogleId: " + error.getMessage()));
    }
 }

}
