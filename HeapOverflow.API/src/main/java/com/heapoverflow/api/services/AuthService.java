package com.heapoverflow.api.services;

import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AuthService {

    public static final ObjectMapper objectMapper= new ObjectMapper();

    public String getJWT(String authCode) throws Exception{
        String url = "https://oauth2.googleapis.com/token";

        String body =
            "code=" + URLDecoder.decode(authCode, StandardCharsets.UTF_8) +
            "&client_id=" + System.getenv("CLIENT_ID") +
            "&client_secret=" + System.getenv("CLIENT_SECRET") +
            "&redirect_uri=" +  "http://localhost:" + System.getenv("LOCAL_AUTH_PORT") + "/callback" +
            "&grant_type=" + "authorization_code" + 
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
         
          ObjectMapper objectMapper = new ObjectMapper();
          HashMap<String, String> map = objectMapper.readValue(resp.body(), new TypeReference<HashMap<String, String>>() {});
 
          return map.get("id_token");
    }
 
}
