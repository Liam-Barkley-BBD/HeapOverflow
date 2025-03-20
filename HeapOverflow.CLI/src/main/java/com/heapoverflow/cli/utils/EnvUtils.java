package com.heapoverflow.cli.utils;

import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.heapoverflow.cli.constants.EnvConstants;

public class EnvUtils {
    private static final Preferences preferences = Preferences.userRoot().node("jwt_storage");
    private static Map<String, String> EnvVariables = Map.of(
        EnvConstants.LOCAL_AUTH_PORT, "55555",
        EnvConstants.CLIENT_ID, "534038687097-97marabsjsrj0hgkuc43f8vbvg86mlft.apps.googleusercontent.com",
        EnvConstants.REDIRECT_URI, "http://localhost:55555/callback",
        EnvConstants.AUTH_URL, "https://accounts.google.com/o/oauth2/auth",
        EnvConstants.LOCAL_AUTH_TIMEOUT, "60",
        EnvConstants.SERVER_URI, "http://ec2-13-247-55-61.af-south-1.compute.amazonaws.com:8080/"
    );

    public static String getStringEnvOrThrow(String envVar) {
        String value = EnvVariables.get(envVar);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException(
                    "Environment variable " + envVar + " is not set. Please set it before running the application.");
        } else {
            return value;
        }
    }

    public static int getIntEnvOrThrow(String envVar) {
        String value = getStringEnvOrThrow(envVar);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException error) {
            throw new IllegalStateException(
                    "Environment variable " + envVar + " must be a valid integer, but got: " + value);
        }
    }

    public static void setStringEnv(String envVar, String value) {
        System.setProperty(envVar, value);
    }

    public static void deleteEnv(String envVar) {
        System.clearProperty(envVar);
    }

    public static boolean doesEnvExist(String envVar) {
        String value = System.getenv(envVar);
        return !(value == null || value.isEmpty());
    }

    public static void storeValue(String key, String jwt) {
        preferences.put(key, jwt);
    }

    public static void deleteKeys() throws BackingStoreException {
        preferences.clear();
    }

    public static String retrieveValue(String key) {
        return preferences.get(key, "");
    }

    public static boolean doesKeyExist(String key){
        return !(retrieveValue(key).trim().isEmpty());
    }
}
