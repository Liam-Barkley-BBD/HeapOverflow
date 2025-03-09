package com.heapoverflow.networking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRequest {
    public static String make(){
        try {
            String apiUrl = "https://www.googleapis.com/oauth2/v3/userinfo"; // Example API endpoint

            // Create the URL object
            URL url = new URL(apiUrl);

            // Open a connection to the API
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Add Authorization header with the Bearer token
            conn.setRequestProperty("Authorization", "Bearer " + "accessToken");

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Return the response (user data or API response)
            return response.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error calling Google API: " + e.getMessage(), e);
        }
    }
}
