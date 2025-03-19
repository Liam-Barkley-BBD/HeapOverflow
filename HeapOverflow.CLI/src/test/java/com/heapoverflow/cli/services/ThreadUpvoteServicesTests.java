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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ThreadUpvoteServicesTests {

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
    void deleteThreadUpVote_ShouldCallCorrectEndpoint() throws Exception {
        // Arrange
        String threadId = "123";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncDelete(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = ThreadUpvoteServices.deleteThreadUpVote(threadId);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncDelete(
                    baseUrl + ApiEndpointsConstants.API_THREADS_UPVOTES + "?threadId=" + threadId));
        }
    }

    @Test
    void postThreadUpVote_ShouldCallCorrectEndpointWithParams() throws Exception {
        // Arrange
        int threadId = 123;
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncPost(anyString(), anyMap()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = ThreadUpvoteServices.postThreadUpVote(threadId);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncPost(
                    eq(baseUrl + ApiEndpointsConstants.API_THREADS_UPVOTES),
                    eq(Map.of("threadId", threadId))));
        }
    }
    
    @Test
    void deleteThreadUpVote_WithException_ShouldPropagateException() {
        // Arrange
        String threadId = "123";
        Exception expectedException = new RuntimeException("Test exception");
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncDelete(anyString()))
                    .thenThrow(expectedException);

            // Act & Assert
            Exception actualException = assertThrows(
                    RuntimeException.class,
                    () -> ThreadUpvoteServices.deleteThreadUpVote(threadId)
            );
            
            assertEquals(expectedException, actualException);
        }
    }
    
    @Test
    void postThreadUpVote_WithException_ShouldPropagateException() {
        // Arrange
        int threadId = 123;
        Exception expectedException = new RuntimeException("Test exception");
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncPost(anyString(), anyMap()))
                    .thenThrow(expectedException);

            // Act & Assert
            Exception actualException = assertThrows(
                    RuntimeException.class,
                    () -> ThreadUpvoteServices.postThreadUpVote(threadId)
            );
            
            assertEquals(expectedException, actualException);
        }
    }
}