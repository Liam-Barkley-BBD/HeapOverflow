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
import org.mockito.ArgumentMatcher;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ThreadsServiceTests {

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
    void getThreads_WithoutSearch_ShouldCallCorrectEndpoint() throws Exception {
        // Arrange
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncGet(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = ThreadsService.getThreads(null, 0, 10);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncGet(baseUrl + ApiEndpointsConstants.API_THREADS + "?page=0&size=10"));
        }
    }

    @Test
    void getThreads_WithSearch_ShouldIncludeSearchParameter() throws Exception {
        // Arrange
        String searchText = "test";
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncGet(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = ThreadsService.getThreads(searchText, 0, 10);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncGet(
                    baseUrl + ApiEndpointsConstants.API_THREADS + "?page=0&size=10&searchText=" + searchText));
        }
    }

    @Test
    void postThread_ShouldCallCorrectEndpointWithParams() throws Exception {
        // Arrange
        String title = "Test Title";
        String description = "Test Description";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncPost(anyString(), anyMap()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = ThreadsService.postThread(title, description);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncPost(
                    eq(baseUrl + ApiEndpointsConstants.API_THREADS),
                    eq(Map.of("title", title, "description", description))));
        }
    }

    @Test
    void getThreadsById_ShouldCallCorrectEndpoint() throws Exception {
        // Arrange
        String threadId = "123";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncGet(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = ThreadsService.getThreadsById(threadId);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncGet(baseUrl + ApiEndpointsConstants.API_THREADS_ID + threadId));
        }
    }

    @Test
    void patchThread_WithTitleAndDescription_ShouldCallCorrectEndpoint() throws Exception {
        // Arrange
        String threadId = "123";
        String title = "Updated Title";
        String description = "Updated Description";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncPatch(anyString(), anyMap()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = ThreadsService.patchThread(threadId, title, description, false);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncPatch(
                    eq(baseUrl + ApiEndpointsConstants.API_THREADS_ID + threadId),
                    argThat((ArgumentMatcher<Map<String, Object>>) map -> 
                        map.containsKey("title") && map.get("title").equals(title) &&
                        map.containsKey("description") && map.get("description").equals(description) &&
                        map.size() == 2
                    )
            ));
        }
    }

    @Test
    void patchThread_WithCloseThread_ShouldIncludeClosedAtField() throws Exception {
        // Arrange
        String threadId = "123";
        String title = "Updated Title";
        String description = "Updated Description";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncPatch(anyString(), anyMap()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = ThreadsService.patchThread(threadId, title, description, true);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncPatch(
                    eq(baseUrl + ApiEndpointsConstants.API_THREADS_ID + threadId),
                    argThat((ArgumentMatcher<Map<String, Object>>) map -> 
                        map.containsKey("title") && map.get("title").equals(title) &&
                        map.containsKey("description") && map.get("description").equals(description) &&
                        map.containsKey("closedAt") && map.get("closedAt") instanceof String &&
                        map.size() == 3
                    )
            ));
        }
    }

    @Test
    void deleteThread_ShouldCallCorrectEndpoint() throws Exception {
        // Arrange
        String threadId = "123";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncDelete(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = ThreadsService.deleteThread(threadId);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncDelete(baseUrl + ApiEndpointsConstants.API_THREADS_ID + threadId));
        }
    }
    
    @Test
    void getThreads_WithException_ShouldPropagateException() {
        // Arrange
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
                    () -> ThreadsService.getThreads(null, 0, 10)
            );
            
            assertEquals(expectedException, actualException);
        }
    }
}