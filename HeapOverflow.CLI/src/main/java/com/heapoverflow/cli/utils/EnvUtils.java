package com.heapoverflow.cli.utils;

public class EnvUtils {
    public static String getStringEnvOrThrow(String envVar) {
        String value = System.getenv(envVar);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("Environment variable " + envVar + " is not set. Please set it before running the application.");
        } else{
            return value;
        }
    }
    
    public static int getIntEnvOrThrow(String envVar) {
        String value = getStringEnvOrThrow(envVar);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException error) {
            throw new IllegalStateException("Environment variable " + envVar + " must be a valid integer, but got: " + value);
        }
    }

    public static void setStringEnv(String envVar, String value){
        System.setProperty(envVar, value);
    }

    public static void deleteEnv(String envVar){
        System.clearProperty(envVar);
    }

    public static boolean doesEnvExist(String envVar){
        String value = System.getenv(envVar);
        return !(value == null || value.isEmpty());
    }
}
