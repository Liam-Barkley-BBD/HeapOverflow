package com.heapoverflow.cli.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heapoverflow.cli.constants.ApiEndpointsConstants;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.HttpUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    private String baseUrl;
    private ObjectMapper objectMapper;
    private JsonNode mockResponse;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:8080";
        objectMapper = new ObjectMapper();
        mockResponse = objectMapper.createObjectNode();
    }

    @Test
    void getUsers_WithUsernameAndEmail_ShouldCallCorrectEndpoint() throws Exception {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";
        int page = 0;
        int size = 10;
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncGet(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = UserServices.getUsers(username, email, page, size);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncGet(
                    baseUrl + ApiEndpointsConstants.API_USERS + "?username=" + username + 
                    "&email=" + email + "&page=" + page + "&size=" + size));
        }
    }

    @Test
    void getUsers_WithUsernameOnly_ShouldCallCorrectEndpoint() throws Exception {
        // Arrange
        String username = "testuser";
        String email = "";
        int page = 0;
        int size = 10;
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncGet(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = UserServices.getUsers(username, email, page, size);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncGet(
                    baseUrl + ApiEndpointsConstants.API_USERS + "?username=" + username + 
                    "&page=" + page + "&size=" + size));
        }
    }

    @Test
    void getUsers_WithEmailOnly_ShouldCallCorrectEndpoint() throws Exception {
        // Arrange
        String username = "";
        String email = "test@example.com";
        int page = 0;
        int size = 10;
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncGet(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = UserServices.getUsers(username, email, page, size);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncGet(
                    baseUrl + ApiEndpointsConstants.API_USERS + "?email=" + email + 
                    "&page=" + page + "&size=" + size));
        }
    }

    @Test
    void getUsers_WithoutUsernameAndEmail_ShouldCallCorrectEndpoint() throws Exception {
        // Arrange
        String username = "";
        String email = "";
        int page = 0;
        int size = 10;
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncGet(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = UserServices.getUsers(username, email, page, size);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncGet(
                    baseUrl + ApiEndpointsConstants.API_USERS + "?page=" + page + "&size=" + size));
        }
    }

    @Test
    void getUsersByGoogleId_ShouldCallCorrectEndpoint() throws Exception {
        // Arrange
        String userGoogleId = "google123";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncGet(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = UserServices.getUsersByGoogleId(userGoogleId);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncGet(
                    baseUrl + ApiEndpointsConstants.API_USERS_GID + userGoogleId));
        }
    }

    @Test
    void getUsers_WithException_ShouldPropagateException() {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";
        int page = 0;
        int size = 10;
        Exception expectedException = new RuntimeException("Test exception");
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncGet(anyString()))
                    .thenThrow(expectedException);

            // Act & Assert
            Exception actualException = assertThrows(
                    RuntimeException.class,
                    () -> UserServices.getUsers(username, email, page, size)
            );
            
            assertEquals(expectedException, actualException);
        }
    }

    @Test
    void getUsersByGoogleId_WithException_ShouldPropagateException() {
        // Arrange
        String userGoogleId = "google123";
        Exception expectedException = new RuntimeException("Test exception");
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncGet(anyString()))
                    .thenThrow(expectedException);

            // Act & Assert
            Exception actualException = assertThrows(
                    RuntimeException.class,
                    () -> UserServices.getUsersByGoogleId(userGoogleId)
            );
            
            assertEquals(expectedException, actualException);
        }
    }
}