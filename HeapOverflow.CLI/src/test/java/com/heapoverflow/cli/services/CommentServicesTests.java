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
public class CommentServicesTests {

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
    void getComments_WithThreadId_ShouldIncludeThreadIdParameter() throws Exception {
        // Arrange
        int page = 0;
        int size = 10;
        String threadId = "thread123";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncGet(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = CommentsServices.getComments(page, size, threadId);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncGet(
                    baseUrl + ApiEndpointsConstants.API_COMMENTS + "?page=0&size=10&threadId=" + threadId));
        }
    }

    @Test
    void getComments_WithoutThreadId_ShouldCallCorrectEndpoint() throws Exception {
        // Arrange
        int page = 0;
        int size = 10;
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncGet(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = CommentsServices.getComments(page, size, null);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncGet(
                    baseUrl + ApiEndpointsConstants.API_COMMENTS + "?page=0&size=10"));
        }
    }

    @Test
    void getComments_WithEmptyThreadId_ShouldCallCorrectEndpoint() throws Exception {
        // Arrange
        int page = 0;
        int size = 10;
        String threadId = "";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncGet(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = CommentsServices.getComments(page, size, threadId);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncGet(
                    baseUrl + ApiEndpointsConstants.API_COMMENTS + "?page=0&size=10"));
        }
    }

    @Test
    void getCommentById_ShouldCallCorrectEndpoint() throws Exception {
        // Arrange
        String commentId = "comment123";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncGet(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = CommentsServices.getCommentById(commentId);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncGet(
                    baseUrl + ApiEndpointsConstants.API_COMMENT_ID + commentId));
        }
    }

    @Test
    void postComment_ShouldCallCorrectEndpointWithParams() throws Exception {
        // Arrange
        String content = "Test Comment";
        String threadId = "thread123";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncPost(anyString(), anyMap()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = CommentsServices.postComment(content, threadId);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncPost(
                    eq(baseUrl + ApiEndpointsConstants.API_COMMENTS),
                    eq(Map.of("content", content, "threadId", threadId))));
        }
    }

    @Test
    void patchComment_ShouldCallCorrectEndpointWithParams() throws Exception {
        // Arrange
        String content = "Updated Comment";
        String commentId = "comment123";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncPatch(anyString(), anyMap()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = CommentsServices.patchComment(content, commentId);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncPatch(
                    eq(baseUrl + ApiEndpointsConstants.API_COMMENTS + "/" + commentId),
                    eq(Map.of("content", content))));
        }
    }

    @Test
    void deleteComment_ShouldCallCorrectEndpoint() throws Exception {
        // Arrange
        String commentId = "comment123";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncDelete(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = CommentsServices.deleteComment(commentId);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncDelete(
                    baseUrl + ApiEndpointsConstants.API_COMMENT_ID + commentId));
        }
    }
    
    @Test
    void getComments_WithException_ShouldPropagateException() {
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
                    () -> CommentsServices.getComments(0, 10, "thread123")
            );
            
            assertEquals(expectedException, actualException);
        }
    }
}