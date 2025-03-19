package com.heapoverflow.api.services;

import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heapoverflow.api.entities.User;

@Service
public class AuthService {

    private final UserService userService;
    public static final ObjectMapper objectMapper = new ObjectMapper();

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public String authenticateUser(String authCode) throws Exception {
        String jwt = getJWT(authCode);
        User user = decodeJWT(jwt);

        if (!userService.userExistsById(user.getId())) {
            userService.createUser(user);
            System.out.println("Created new user ID: " + user.getId());
        } else {
            System.out.println("Authorized existing user ID: " + user.getId());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> jwtResponse = new HashMap<>();
        jwtResponse.put("jwt", jwt);

        return objectMapper.writeValueAsString(jwtResponse);
    }

    private String getJWT(String authCode) throws Exception {
        String url = "https://oauth2.googleapis.com/token";

        String body =
            "code=" + URLDecoder.decode(authCode, StandardCharsets.UTF_8) +
            "&client_id=" + System.getenv("CLIENT_ID") +
            "&client_secret=" + System.getenv("CLIENT_SECRET") +
            "&redirect_uri=" + "http://localhost:" + System.getenv("LOCAL_AUTH_PORT") + "/callback" +
            "&grant_type=authorization_code" +
            "&scope=openid%20email%20profile";

        System.out.println("The body of the token request:\n" + body);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .header("Content-Type", "application/x-www-form-urlencoded")
          .POST(HttpRequest.BodyPublishers.ofString(body))
          .build();

        HttpResponse<String> resp = client.send(request, BodyHandlers.ofString());
        System.out.println("RESPONSE: " + resp);

        HashMap<String, String> map = objectMapper.readValue(resp.body(), new TypeReference<HashMap<String, String>>() {});

        return map.get("id_token");
    }

    private User decodeJWT(String jwt) throws Exception {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
        else {
            // Valid jwt
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        Map<String, Object> payloadMap = objectMapper.readValue(payloadJson, new TypeReference<Map<String, Object>>() {});

        String sub = (String) payloadMap.get("sub");
        String email = (String) payloadMap.get("email");
        String username = (String) payloadMap.get("name");

        return new User(sub, username, email);
    }
}