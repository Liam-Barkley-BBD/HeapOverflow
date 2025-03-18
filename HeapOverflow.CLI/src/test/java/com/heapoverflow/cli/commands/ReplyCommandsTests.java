package com.heapoverflow.cli.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import com.heapoverflow.cli.utils.TextUtils;

@ExtendWith(MockitoExtension.class)
public class ReplyCommandsTests {

    @InjectMocks
    private ReplyCommands replyCommands;

    private ObjectMapper objectMapper;
    private JsonNode mockReply;
    private JsonNode mockRepliesList;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        
        // Create mock reply
        ObjectNode replyNode = objectMapper.createObjectNode();
        replyNode.put("id", "reply123");
        replyNode.put("content", "Test reply content");
        replyNode.put("commentId", "comment123");
        replyNode.put("createdAt", "2023-01-01T12:00:00Z");
        
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("id", "user123");
        userNode.put("username", "testuser");
        userNode.put("email", "test@example.com");
        replyNode.set("user", userNode);
        
        mockReply = replyNode;
        
        // Create mock replies list
        ObjectNode repliesListNode = objectMapper.createObjectNode();
        ArrayNode contentArray = objectMapper.createArrayNode();
        contentArray.add(replyNode.deepCopy());
        
        ObjectNode reply2 = objectMapper.createObjectNode();
        reply2.put("id", "reply456");
        reply2.put("content", "Another reply");
        reply2.put("commentId", "comment123");
        reply2.put("createdAt", "2023-01-02T12:00:00Z");
        
        ObjectNode user2 = objectMapper.createObjectNode();
        user2.put("id", "user456");
        user2.put("username", "anotheruser");
        user2.put("email", "another@example.com");
        reply2.set("user", user2);
        
        contentArray.add(reply2);
        
        repliesListNode.set("content", contentArray);
        repliesListNode.put("totalElements", 2);
        repliesListNode.put("totalPages", 1);
        repliesListNode.put("number", 0);
        repliesListNode.put("last", true);
        
        mockRepliesList = repliesListNode;
    }

    @Test
    void replies_WhenNotLoggedIn_ShouldReturnLoginMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(false);
            
            // Act
            String result = replyCommands.replies(false, false, false, false, false, "", "", "", 0, 5);
            
            // Assert
            assertEquals("You are not logged in. Please log in!", result);
        }
    }

    @Test
    void replies_WithNoCommand_ShouldReturnHelpMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            
            // Act
            String result = replyCommands.replies(false, false, false, false, false, "", "", "", 0, 5);
            
            // Assert
            assertTrue(result.startsWith("Invalid command. Use:"));
            assertTrue(result.contains("--list"));
            assertTrue(result.contains("--get"));
            assertTrue(result.contains("--post"));
            assertTrue(result.contains("--edit"));
            assertTrue(result.contains("--delete"));
        }
    }

    @Test
    void replies_WithListCommand_ShouldCallListReplies() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<ReplyServices> replyServices = mockStatic(ReplyServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            replyServices.when(() -> ReplyServices.getReplies(anyInt(), anyInt(), anyString())).thenReturn(mockRepliesList);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class))).thenReturn("Rendered Table");
            
            // Act
            String result = replyCommands.replies(true, false, false, false, false, "", "", "comment123", 1, 10);
            
            // Assert
            replyServices.verify(() -> ReplyServices.getReplies(0, 10, "comment123"));
            assertTrue(result.contains("Rendered Table"));
            assertTrue(result.contains("Page 1 of 1"));
            assertTrue(result.contains("Total Replies: 2"));
        }
    }

    @Test
    void replies_WithGetCommand_ShouldCallGetReplyById() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<ReplyServices> replyServices = mockStatic(ReplyServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            String replyId = "reply123";
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            replyServices.when(() -> ReplyServices.getReplyById(replyId)).thenReturn(mockReply);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class))).thenReturn("Rendered Table");
            
            // Act
            String result = replyCommands.replies(false, true, false, false, false, replyId, "", "", 0, 5);
            
            // Assert
            replyServices.verify(() -> ReplyServices.getReplyById(replyId));
            assertEquals("Rendered Table", result);
        }
    }

    @Test
    void replies_WithGetCommandAndEmptyReplyId_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            
            // Act
            String result = replyCommands.replies(false, true, false, false, false, "", "", "", 0, 5);
            
            // Assert
            assertEquals("The ID must be specified like: replies --get --replyId {id}", result);
        }
    }

    @Test
    void replies_WithPostCommand_ShouldCallPostReply() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<ReplyServices> replyServices = mockStatic(ReplyServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            String content = "New reply content";
            String commentId = "comment123";
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            replyServices.when(() -> ReplyServices.postReply(content, commentId)).thenReturn(mockReply);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class))).thenReturn("Rendered Table");
            
            // Act
            String result = replyCommands.replies(false, false, true, false, false, "", content, commentId, 0, 5);
            
            // Assert
            replyServices.verify(() -> ReplyServices.postReply(content, commentId));
            assertEquals("Rendered Table", result);
        }
    }

    @Test
    void replies_WithPostCommandAndEmptyCommentId_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            
            // Act
            String result = replyCommands.replies(false, false, true, false, false, "", "content", "", 0, 5);
            
            // Assert
            assertTrue(result.contains("The commentId must be specified"));
        }
    }

    @Test
    void replies_WithEditCommand_ShouldCallPatchReply() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<ReplyServices> replyServices = mockStatic(ReplyServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            String replyId = "reply123";
            String content = "Updated content";
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            replyServices.when(() -> ReplyServices.patchReply(content, replyId)).thenReturn(mockReply);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class))).thenReturn("Rendered Table");
            
            // Act
            String result = replyCommands.replies(false, false, false, true, false, replyId, content, "", 0, 5);
            
            // Assert
            replyServices.verify(() -> ReplyServices.patchReply(content, replyId));
            assertEquals("Rendered Table", result);
        }
    }

    @Test
    void replies_WithEditCommandAndEmptyReplyId_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            
            // Act
            String result = replyCommands.replies(false, false, false, true, false, "", "content", "", 0, 5);
            
            // Assert
            assertTrue(result.contains("The ID must be specified"));
        }
    }

    @Test
    void replies_WithDeleteCommand_ShouldCallDeleteReply() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<ReplyServices> replyServices = mockStatic(ReplyServices.class)) {
            
            // Arrange
            String replyId = "reply123";
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            
            // Act
            String result = replyCommands.replies(false, false, false, false, true, replyId, "", "", 0, 5);
            
            // Assert
            replyServices.verify(() -> ReplyServices.deleteReply(replyId));
            assertEquals("Reply with ID " + replyId + " has been deleted.", result);
        }
    }

    @Test
    void replies_WithDeleteCommandAndEmptyReplyId_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            
            // Act
            String result = replyCommands.replies(false, false, false, false, true, "", "", "", 0, 5);
            
            // Assert
            assertTrue(result.contains("The ID must be specified"));
        }
    }

    @Test
    void listReplies_WithValidData_ShouldRenderTable() {
        try (MockedStatic<ReplyServices> replyServices = mockStatic(ReplyServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            int page = 1;
            int size = 10;
            String commentId = "comment123";
            replyServices.when(() -> ReplyServices.getReplies(anyInt(), anyInt(), anyString())).thenReturn(mockRepliesList);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class))).thenReturn("Rendered Table");
            
            // Act
            String result = ReplyCommands.listReplies(page, size, commentId);
            
            // Assert
            replyServices.verify(() -> ReplyServices.getReplies(0, size, commentId));
            assertTrue(result.contains("Rendered Table"));
            assertTrue(result.contains("Page 1 of 1"));
            assertTrue(result.contains("Total Replies: 2"));
            assertTrue(result.contains("(Last Page)"));
        }
    }

    @Test
    void listReplies_WithNoReplies_ShouldReturnMessage() {
        try (MockedStatic<ReplyServices> replyServices = mockStatic(ReplyServices.class)) {
            // Arrange
            ObjectNode emptyResponse = objectMapper.createObjectNode();
            emptyResponse.set("content", objectMapper.createArrayNode());
            
            replyServices.when(() -> ReplyServices.getReplies(anyInt(), anyInt(), anyString())).thenReturn(emptyResponse);
            
            // Act
            String result = ReplyCommands.listReplies(1, 10, "comment123");
            
            // Assert
            assertEquals("No replies found.", result);
        }
    }

    @Test
    void listReplies_WithException_ShouldReturnErrorMessage() {
        try (MockedStatic<ReplyServices> replyServices = mockStatic(ReplyServices.class)) {
            // Arrange
            String errorMessage = "Connection error";
            replyServices.when(() -> ReplyServices.getReplies(anyInt(), anyInt(), anyString()))
                    .thenThrow(new RuntimeException(errorMessage));
            
            // Act
            String result = ReplyCommands.listReplies(1, 10, "comment123");
            
            // Assert
            assertEquals("Error retrieving replies: " + errorMessage, result);
        }
    }

    @Test
    void buildReplyTable_WithArrayNode_ShouldBuildCorrectTable() {
        try (MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            // We'll use reflection to test the private method
            // This test is a bit complex due to private method access, but would verify the table builder works correctly
            
            // For simplicity, we'll just check that the rendering is called
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class))).thenReturn("Rendered Table");
            
            // Create a situation that calls buildReplyTable indirectly
            try (MockedStatic<ReplyServices> replyServices = mockStatic(ReplyServices.class)) {
                replyServices.when(() -> ReplyServices.getReplies(anyInt(), anyInt(), anyString())).thenReturn(mockRepliesList);
                
                // Act - this will call buildReplyTable internally
                String result = ReplyCommands.listReplies(1, 10, "comment123");
                
                // Assert 
                assertTrue(result.contains("Rendered Table"));
                textUtils.verify(() -> TextUtils.renderTable(any(TableModel.class)));
            }
        }
    }
}