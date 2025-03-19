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
public class ReplyServicesTests {

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
    void getReplies_WithCommentId_ShouldIncludeCommentIdParameter() throws Exception {
        // Arrange
        int page = 0;
        int size = 10;
        String commentId = "comment123";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncGet(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = ReplyServices.getReplies(page, size, commentId);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncGet(
                    baseUrl + ApiEndpointsConstants.API_REPLIES + "?page=0&size=10&commentId=" + commentId));
        }
    }

    @Test
    void getReplies_WithoutCommentId_ShouldCallCorrectEndpoint() throws Exception {
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
            JsonNode result = ReplyServices.getReplies(page, size, null);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncGet(
                    baseUrl + ApiEndpointsConstants.API_REPLIES + "?page=0&size=10"));
        }
    }

    @Test
    void getReplies_WithEmptyCommentId_ShouldCallCorrectEndpoint() throws Exception {
        // Arrange
        int page = 0;
        int size = 10;
        String commentId = "";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncGet(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = ReplyServices.getReplies(page, size, commentId);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncGet(
                    baseUrl + ApiEndpointsConstants.API_REPLIES + "?page=0&size=10"));
        }
    }

    @Test
    void getReplyById_ShouldCallCorrectEndpoint() throws Exception {
        // Arrange
        String replyId = "reply123";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncGet(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = ReplyServices.getReplyById(replyId);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncGet(
                    baseUrl + ApiEndpointsConstants.API_REPLIES_ID + replyId));
        }
    }

    @Test
    void postReply_ShouldCallCorrectEndpointWithParams() throws Exception {
        // Arrange
        String content = "Test Reply";
        String commentId = "comment123";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncPost(anyString(), anyMap()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = ReplyServices.postReply(content, commentId);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncPost(
                    eq(baseUrl + ApiEndpointsConstants.API_REPLIES),
                    eq(Map.of("content", content, "commentId", commentId))));
        }
    }

    @Test
    void patchReply_ShouldCallCorrectEndpointWithParams() throws Exception {
        // Arrange
        String content = "Updated Reply";
        String replyId = "reply123";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncPatch(anyString(), anyMap()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = ReplyServices.patchReply(content, replyId);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncPatch(
                    eq(baseUrl + ApiEndpointsConstants.API_REPLIES + "/" + replyId),
                    eq(Map.of("content", content))));
        }
    }

    @Test
    void deleteReply_ShouldCallCorrectEndpoint() throws Exception {
        // Arrange
        String replyId = "reply123";
        
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<HttpUtils> httpUtils = mockStatic(HttpUtils.class)) {
            
            envUtils.when(() -> EnvUtils.getStringEnvOrThrow(EnvConstants.SERVER_URI))
                    .thenReturn(baseUrl);
            httpUtils.when(() -> HttpUtils.syncDelete(anyString()))
                    .thenReturn(mockResponse);

            // Act
            JsonNode result = ReplyServices.deleteReply(replyId);

            // Assert
            assertEquals(mockResponse, result);
            httpUtils.verify(() -> HttpUtils.syncDelete(
                    baseUrl + ApiEndpointsConstants.API_REPLIES_ID + replyId));
        }
    }
    
    @Test
    void getReplies_WithException_ShouldPropagateException() {
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
                    () -> ReplyServices.getReplies(0, 10, "comment123")
            );
            
            assertEquals(expectedException, actualException);
        }
    }
}