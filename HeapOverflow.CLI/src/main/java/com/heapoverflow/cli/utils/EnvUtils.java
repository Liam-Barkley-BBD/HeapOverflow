package com.heapoverflow.cli.utils;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class EnvUtils {
    private static final Preferences preferences = Preferences.userRoot().node("jwt_storage");

    public static String getStringEnvOrThrow(String envVar) {
        String value = System.getenv(envVar);
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

    public static void storeJwt(String jwt) {
        preferences.put("jwt", jwt);
    }

    public static void deleteJWT() throws BackingStoreException {
        preferences.clear();
    }

    public static String retrieveJwt() {
        return preferences.get("jwt", ""); // Returns null if not set
    }
}
