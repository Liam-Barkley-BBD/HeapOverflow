package com.heapoverflow.cli.utils;

public class EnvUtils {
    public static String getStringEnvOrThrow(String envVar) {
        String value = System.getenv(envVar);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("Environment variable " + envVar + " is not set. Please set it before running the application.");
        }
        return value;
    }
    
    public static int getIntEnvOrThrow(String envVar) {
        String value = getStringEnvOrThrow(envVar);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Environment variable " + envVar + " must be a valid integer, but got: " + value);
        }
    }
}
