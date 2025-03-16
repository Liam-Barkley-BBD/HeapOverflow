package com.heapoverflow.cli.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TokenUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String decodeJWT(String key, String jwt) throws Exception {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token");
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        Map<String, Object> payloadMap = objectMapper.readValue(payloadJson, new TypeReference<Map<String, Object>>() {});

        String value = (String) payloadMap.get(key);

        return value;
    }
}
