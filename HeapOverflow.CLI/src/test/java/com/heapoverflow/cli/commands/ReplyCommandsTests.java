package com.heapoverflow.cli.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.shell.table.TableModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.ReplyServices;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.FlagsCheckUtils;
import com.heapoverflow.cli.utils.TextUtils;

@ExtendWith(MockitoExtension.class)
public class ReplyCommandsTests {

    @InjectMocks
    private ReplyCommands replyCommands;

    private ObjectMapper objectMapper;
    private JsonNode singleReplyNode;
    private JsonNode repliesListNode;
    private JsonNode emptyRepliesListNode;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        
        // Create a single reply node
        singleReplyNode = createReplyNode("reply123", "Test reply", "user123", "testuser", 
                "test@example.com", "2025-03-15T10:00:00Z", "comment123");
        
        // Create a list of replies
        ArrayNode repliesArray = objectMapper.createArrayNode();
        repliesArray.add(createReplyNode("reply1", "First reply", "user1", "user1", 
                "user1@example.com", "2025-03-15T10:00:00Z", "comment123"));
        repliesArray.add(createReplyNode("reply2", "Second reply", "user2", "user2", 
                "user2@example.com", "2025-03-15T11:00:00Z", "comment123"));
        
        // Create replies list response
        ObjectNode repliesResponseNode = objectMapper.createObjectNode();
        repliesResponseNode.set("content", repliesArray);
        repliesResponseNode.put("totalElements", 2);
        repliesResponseNode.put("totalPages", 1);
        repliesResponseNode.put("number", 0);
        repliesResponseNode.put("last", true);
        repliesListNode = repliesResponseNode;
        
        // Create empty replies list response
        ObjectNode emptyRepliesResponseNode = objectMapper.createObjectNode();
        emptyRepliesResponseNode.set("content", objectMapper.createArrayNode());
        emptyRepliesResponseNode.put("totalElements", 0);
        emptyRepliesResponseNode.put("totalPages", 0);
        emptyRepliesResponseNode.put("number", 0);
        emptyRepliesResponseNode.put("last", true);
        emptyRepliesListNode = emptyRepliesResponseNode;
    }

    private JsonNode createReplyNode(String id, String content, String userId, String username, 
            String email, String createdAt, String commentId) {
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("id", userId);
        userNode.put("username", username);
        userNode.put("email", email);
        
        ObjectNode replyNode = objectMapper.createObjectNode();
        replyNode.put("id", id);
        replyNode.put("content", content);
        replyNode.put("createdAt", createdAt);
        replyNode.put("commentId", commentId);
        replyNode.set("user", userNode);
        
        return replyNode;
    }

    @Test
    void replies_NotLoggedIn_ShouldReturnLoginMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(false);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForReplies(
                    eq(true), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list"));
            
            // Act
            String result = replyCommands.replies(true, false, false, false, false, 
                    Optional.empty(), Optional.empty(), Optional.empty(), 1, 10);
            
            // Assert
            assertEquals("You are not logged in. Please log in!", result);
        }
    }

    @Test
    void replies_MultipleFlags_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForReplies(
                    eq(true), eq(true), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list", "get"));
            
            // Act
            String result = replyCommands.replies(true, true, false, false, false, 
                    Optional.empty(), Optional.empty(), Optional.empty(), 1, 10);
            
            // Assert
            assertTrue(result.startsWith("You cannot use multiple action based flags at once:"));
            assertTrue(result.contains("list") && result.contains("get"));
        }
    }

    @Test
    void replies_NoOptionSelected_ShouldReturnHelpMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForReplies(
                    eq(false), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Collections.emptyList());
            
            // Act
            String result = replyCommands.replies(false, false, false, false, false, 
                    Optional.empty(), Optional.empty(), Optional.empty(), 1, 10);
            
            // Assert
            assertTrue(result.startsWith("Invalid command. Use:"));
        }
    }

    @Test
    void replies_ListWithNoReplies_ShouldReturnNoRepliesMessage() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<ReplyServices> replyServices = mockStatic(ReplyServices.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForReplies(
                    eq(true), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list"));
            replyServices.when(() -> ReplyServices.getReplies(0, 10, "comment123"))
                    .thenReturn(emptyRepliesListNode);
            
            // Act
            String result = replyCommands.replies(true, false, false, false, false, 
                    Optional.empty(), Optional.empty(), Optional.of("comment123"), 1, 10);
            
            // Assert
            assertEquals("No replies found.", result);
        }
    }

    @Test
    void replies_ListWithReplies_ShouldReturnRepliesList() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<ReplyServices> replyServices = mockStatic(ReplyServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForReplies(
                    eq(true), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list"));
            replyServices.when(() -> ReplyServices.getReplies(0, 10, "comment123"))
                    .thenReturn(repliesListNode);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered Replies Table");
            
            // Act
            String result = replyCommands.replies(true, false, false, false, false, 
                    Optional.empty(), Optional.empty(), Optional.of("comment123"), 1, 10);
            
            // Assert
            assertTrue(result.startsWith("Rendered Replies Table"));
            assertTrue(result.contains("Page 1 of 1 | Total Replies: 2 (Last Page)"));
        }
    }

    @Test
    void replies_GetWithNoId_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForReplies(
                    eq(false), eq(true), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("get"));
            
            // Act
            String result = replyCommands.replies(false, true, false, false, false, 
                    Optional.empty(), Optional.empty(), Optional.empty(), 1, 10);
            
            // Assert
            assertTrue(result.contains("The ID must be specified"));
        }
    }

    @Test
    void replies_GetWithId_ShouldReturnReply() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<ReplyServices> replyServices = mockStatic(ReplyServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForReplies(
                    eq(false), eq(true), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("get"));
            replyServices.when(() -> ReplyServices.getReplyById("reply123"))
                    .thenReturn(singleReplyNode);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered Reply Table");
            
            // Act
            String result = replyCommands.replies(false, true, false, false, false, 
                    Optional.of("reply123"), Optional.empty(), Optional.empty(), 1, 10);
            
            // Assert
            assertEquals("Rendered Reply Table", result);
        }
    }

    @Test
    void replies_PostWithNoCommentId_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForReplies(
                    eq(false), eq(false), eq(true), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("post"));
            
            // Act
            String result = replyCommands.replies(false, false, true, false, false, 
                    Optional.empty(), Optional.of("Test reply"), Optional.empty(), 1, 10);
            
            // Assert
            assertTrue(result.contains("The commentId must be specified"));
        }
    }

    @Test
    void replies_PostWithValidData_ShouldReturnNewReply() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<ReplyServices> replyServices = mockStatic(ReplyServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForReplies(
                    eq(false), eq(false), eq(true), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("post"));
            replyServices.when(() -> ReplyServices.postReply("Test reply", "comment123"))
                    .thenReturn(singleReplyNode);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered New Reply Table");
            
            // Act
            String result = replyCommands.replies(false, false, true, false, false, 
                    Optional.empty(), Optional.of("Test reply"), Optional.of("comment123"), 1, 10);
            
            // Assert
            assertEquals("Rendered New Reply Table", result);
        }
    }

    @Test
    void replies_EditWithNoId_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForReplies(
                    eq(false), eq(false), eq(false), eq(true), eq(false)))
                    .thenReturn(java.util.Arrays.asList("edit"));
            
            // Act
            String result = replyCommands.replies(false, false, false, true, false, 
                    Optional.empty(), Optional.of("Updated reply"), Optional.empty(), 1, 10);
            
            // Assert
            assertTrue(result.contains("The ID must be specified"));
        }
    }

    @Test
    void replies_EditWithValidData_ShouldReturnUpdatedReply() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<ReplyServices> replyServices = mockStatic(ReplyServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForReplies(
                    eq(false), eq(false), eq(false), eq(true), eq(false)))
                    .thenReturn(java.util.Arrays.asList("edit"));
            replyServices.when(() -> ReplyServices.patchReply("Updated reply", "reply123"))
                    .thenReturn(singleReplyNode);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered Updated Reply Table");
            
            // Act
            String result = replyCommands.replies(false, false, false, true, false, 
                    Optional.of("reply123"), Optional.of("Updated reply"), Optional.empty(), 1, 10);
            
            // Assert
            assertEquals("Rendered Updated Reply Table", result);
        }
    }

    @Test
    void replies_DeleteWithNoId_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForReplies(
                    eq(false), eq(false), eq(false), eq(false), eq(true)))
                    .thenReturn(java.util.Arrays.asList("delete"));
            
            // Act
            String result = replyCommands.replies(false, false, false, false, true, 
                    Optional.empty(), Optional.empty(), Optional.empty(), 1, 10);
            
            // Assert
            assertTrue(result.contains("The ID must be specified"));
        }
    }

    @Test
    void replies_DeleteWithValidId_ShouldReturnSuccessMessage() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<ReplyServices> replyServices = mockStatic(ReplyServices.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForReplies(
                    eq(false), eq(false), eq(false), eq(false), eq(true)))
                    .thenReturn(java.util.Arrays.asList("delete"));
            replyServices.when(() -> ReplyServices.deleteReply("reply123"))
                    .thenReturn(null);
            
            // Act
            String result = replyCommands.replies(false, false, false, false, true, 
                    Optional.of("reply123"), Optional.empty(), Optional.empty(), 1, 10);
            
            // Assert
            assertEquals("Reply with ID reply123 has been deleted.", result);
        }
    }

    @Test
    void listReplies_WithException_ShouldReturnErrorMessage() throws Exception {
        try (MockedStatic<ReplyServices> replyServices = mockStatic(ReplyServices.class)) {
            // Arrange
            replyServices.when(() -> ReplyServices.getReplies(0, 10, "comment123"))
                    .thenThrow(new IOException("Network error"));
            
            // Act
            String result = ReplyCommands.listReplies(1, 10, "comment123");
            
            // Assert
            assertEquals("Error retrieving replies: Network error", result);
        }
    }
}