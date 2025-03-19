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
import com.heapoverflow.cli.services.CommentUpVotesService;
import com.heapoverflow.cli.services.CommentsServices;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.FlagsCheckUtils;
import com.heapoverflow.cli.utils.TextUtils;

@ExtendWith(MockitoExtension.class)
public class CommentCommandsTests {

    @InjectMocks
    private CommentCommands commentCommands;

    private ObjectMapper objectMapper;
    private JsonNode singleCommentNode;
    private JsonNode commentsListNode;
    private JsonNode emptyCommentsListNode;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        
        // Create a single comment node
        singleCommentNode = createCommentNode("comment123", "Test comment", "user123", "testuser", 
                "test@example.com", "2025-03-15T10:00:00Z", 5, "thread123");
        
        // Create a list of comments
        ArrayNode commentsArray = objectMapper.createArrayNode();
        commentsArray.add(createCommentNode("comment1", "First comment", "user1", "user1", 
                "user1@example.com", "2025-03-15T10:00:00Z", 10, "thread123"));
        commentsArray.add(createCommentNode("comment2", "Second comment", "user2", "user2", 
                "user2@example.com", "2025-03-15T11:00:00Z", 5, "thread123"));
        
        // Create comments list response
        ObjectNode commentsResponseNode = objectMapper.createObjectNode();
        commentsResponseNode.set("content", commentsArray);
        commentsResponseNode.put("totalElements", 2);
        commentsResponseNode.put("totalPages", 1);
        commentsResponseNode.put("number", 0);
        commentsResponseNode.put("last", true);
        commentsListNode = commentsResponseNode;
        
        // Create empty comments list response
        ObjectNode emptyCommentsResponseNode = objectMapper.createObjectNode();
        emptyCommentsResponseNode.set("content", objectMapper.createArrayNode());
        emptyCommentsResponseNode.put("totalElements", 0);
        emptyCommentsResponseNode.put("totalPages", 0);
        emptyCommentsResponseNode.put("number", 0);
        emptyCommentsResponseNode.put("last", true);
        emptyCommentsListNode = emptyCommentsResponseNode;
    }

    private JsonNode createCommentNode(String id, String content, String userId, String username, 
            String email, String createdAt, int upvotesCount, String threadId) {
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("id", userId);
        userNode.put("username", username);
        userNode.put("email", email);
        
        ObjectNode commentNode = objectMapper.createObjectNode();
        commentNode.put("id", id);
        commentNode.put("content", content);
        commentNode.put("createdAt", createdAt);
        commentNode.put("commentUpvotesCount", upvotesCount);
        commentNode.put("threadId", threadId);
        commentNode.set("user", userNode);
        
        return commentNode;
    }

    @Test
    void comment_NotLoggedIn_ShouldReturnLoginMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(false);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(true), eq(false), eq(false), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list"));
            
            // Act
            String result = commentCommands.comment(true, false, false, false, false, false, false, 
                    Optional.empty(), Optional.empty(), Optional.empty(), 1, 5);
            
            // Assert
            assertEquals("You are not logged in. Please log in!", result);
        }
    }

    @Test
    void comment_MultipleFlags_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(true), eq(true), eq(false), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list", "get"));
            
            // Act
            String result = commentCommands.comment(true, true, false, false, false, false, false, 
                    Optional.empty(), Optional.empty(), Optional.empty(), 1, 5);
            
            // Assert
            assertTrue(result.startsWith("You cannot use multiple action based flags at once:"));
            assertTrue(result.contains("list") && result.contains("get"));
        }
    }

    @Test
    void comment_NoOptionSelected_ShouldReturnHelpMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(false), eq(false), eq(false), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Collections.emptyList());
            
            // Act
            String result = commentCommands.comment(false, false, false, false, false, false, false, 
                    Optional.empty(), Optional.empty(), Optional.empty(), 1, 5);
            
            // Assert
            assertTrue(result.startsWith("Invalid command. Use:"));
        }
    }

    @Test
    void comment_ListWithNoComments_ShouldReturnNoCommentsMessage() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<CommentsServices> commentsServices = mockStatic(CommentsServices.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(true), eq(false), eq(false), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list"));
            commentsServices.when(() -> CommentsServices.getComments(0, 5, "thread123"))
                    .thenReturn(emptyCommentsListNode);
            
            // Act
            String result = commentCommands.comment(true, false, false, false, false, false, false, 
                    Optional.empty(), Optional.empty(), Optional.of("thread123"), 1, 5);
            
            // Assert
            assertEquals("No comments found.", result);
        }
    }

    @Test
    void comment_ListWithComments_ShouldReturnCommentsList() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<CommentsServices> commentsServices = mockStatic(CommentsServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(true), eq(false), eq(false), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list"));
            commentsServices.when(() -> CommentsServices.getComments(0, 5, "thread123"))
                    .thenReturn(commentsListNode);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered Comments Table");
            
            // Act
            String result = commentCommands.comment(true, false, false, false, false, false, false, 
                    Optional.empty(), Optional.empty(), Optional.of("thread123"), 1, 5);
            
            // Assert
            assertTrue(result.startsWith("Rendered Comments Table"));
            assertTrue(result.contains("Page 1 of 1 | Total Comments: 2 (Last Page)"));
        }
    }

    @Test
    void comment_GetWithNoId_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(false), eq(true), eq(false), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("get"));
            
            // Act
            String result = commentCommands.comment(false, true, false, false, false, false, false, 
                    Optional.empty(), Optional.empty(), Optional.empty(), 1, 5);
            
            // Assert
            assertTrue(result.contains("The id must be specified"));
        }
    }

    @Test
    void comment_GetWithId_ShouldReturnComment() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<CommentsServices> commentsServices = mockStatic(CommentsServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class);
             MockedStatic<ReplyCommands> replyCommands = mockStatic(ReplyCommands.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(false), eq(true), eq(false), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("get"));
            commentsServices.when(() -> CommentsServices.getCommentById("comment123"))
                    .thenReturn(singleCommentNode);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered Comment Table");
            replyCommands.when(() -> ReplyCommands.listReplies(1, 5, "comment123"))
                    .thenReturn("Replies List");
            
            // Act
            String result = commentCommands.comment(false, true, false, false, false, false, false, 
                    Optional.of("comment123"), Optional.empty(), Optional.empty(), 1, 5);
            
            // Assert
            assertEquals("Rendered Comment Table\nReplies for comments: \nReplies List", result);
        }
    }

    @Test
    void comment_PostWithNoThreadId_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(false), eq(false), eq(true), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("post"));
            
            // Act
            String result = commentCommands.comment(false, false, true, false, false, false, false, 
                    Optional.empty(), Optional.of("Test comment"), Optional.empty(), 1, 5);
            
            // Assert
            assertTrue(result.contains("The threadId must be specified"));
        }
    }

    @Test
    void comment_PostWithValidData_ShouldReturnNewComment() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<CommentsServices> commentsServices = mockStatic(CommentsServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(false), eq(false), eq(true), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("post"));
            commentsServices.when(() -> CommentsServices.postComment("Test comment", "thread123"))
                    .thenReturn(singleCommentNode);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered New Comment Table");
            
            // Act
            String result = commentCommands.comment(false, false, true, false, false, false, false, 
                    Optional.empty(), Optional.of("Test comment"), Optional.of("thread123"), 1, 5);
            
            // Assert
            assertEquals("Rendered New Comment Table", result);
        }
    }

    @Test
    void comment_EditWithNoId_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(false), eq(false), eq(false), eq(true), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("edit"));
            
            // Act
            String result = commentCommands.comment(false, false, false, true, false, false, false, 
                    Optional.empty(), Optional.of("Updated comment"), Optional.empty(), 1, 5);
            
            // Assert
            assertTrue(result.contains("The id must be specified"));
        }
    }

    @Test
    void comment_EditWithValidData_ShouldReturnUpdatedComment() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<CommentsServices> commentsServices = mockStatic(CommentsServices.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(false), eq(false), eq(false), eq(true), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("edit"));
            commentsServices.when(() -> CommentsServices.patchComment("Updated comment", "comment123"))
                    .thenReturn(singleCommentNode);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered Updated Comment Table");
            
            // Act
            String result = commentCommands.comment(false, false, false, true, false, false, false, 
                    Optional.of("comment123"), Optional.of("Updated comment"), Optional.empty(), 1, 5);
            
            // Assert
            assertEquals("Rendered Updated Comment Table", result);
        }
    }

    @Test
    void comment_DeleteWithNoId_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(false), eq(false), eq(false), eq(false), eq(true), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("delete"));
            
            // Act
            String result = commentCommands.comment(false, false, false, false, true, false, false, 
                    Optional.empty(), Optional.empty(), Optional.empty(), 1, 5);
            
            // Assert
            assertTrue(result.contains("The id must be specified"));
        }
    }

    @Test
    void comment_DeleteWithValidId_ShouldReturnSuccessMessage() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<CommentsServices> commentsServices = mockStatic(CommentsServices.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(false), eq(false), eq(false), eq(false), eq(true), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("delete"));
            commentsServices.when(() -> CommentsServices.deleteComment("comment123"))
                    .thenReturn(null);
            
            // Act
            String result = commentCommands.comment(false, false, false, false, true, false, false, 
                    Optional.of("comment123"), Optional.empty(), Optional.empty(), 1, 5);
            
            // Assert
            assertEquals("Your comment with commentId comment123 has been deleted", result);
        }
    }

    @Test
    void comment_UpvoteWithNoId_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(false), eq(false), eq(false), eq(false), eq(false), eq(true), eq(false)))
                    .thenReturn(java.util.Arrays.asList("upvote"));
            
            // Act
            String result = commentCommands.comment(false, false, false, false, false, true, false, 
                    Optional.empty(), Optional.empty(), Optional.empty(), 1, 5);
            
            // Assert
            assertTrue(result.contains("The id must be specified"));
        }
    }

    @Test
    void comment_UpvoteWithValidId_ShouldReturnSuccessMessage() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<CommentUpVotesService> upVotesService = mockStatic(CommentUpVotesService.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(false), eq(false), eq(false), eq(false), eq(false), eq(true), eq(false)))
                    .thenReturn(java.util.Arrays.asList("upvote"));
            upVotesService.when(() -> CommentUpVotesService.postCommentUpVote("comment123"))
                    .thenReturn(null);
            
            // Act
            String result = commentCommands.comment(false, false, false, false, false, true, false, 
                    Optional.of("comment123"), Optional.empty(), Optional.empty(), 1, 5);
            
            // Assert
            assertEquals("You upvoted a comment with commentId comment123", result);
        }
    }

    @Test
    void comment_UnupvoteWithNoId_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(false), eq(false), eq(false), eq(false), eq(false), eq(false), eq(true)))
                    .thenReturn(java.util.Arrays.asList("un-upvote"));
            
            // Act
            String result = commentCommands.comment(false, false, false, false, false, false, true, 
                    Optional.empty(), Optional.empty(), Optional.empty(), 1, 5);
            
            // Assert
            assertTrue(result.contains("The id must be specified"));
        }
    }

    @Test
    void comment_UnupvoteWithValidId_ShouldReturnSuccessMessage() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<CommentUpVotesService> upVotesService = mockStatic(CommentUpVotesService.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(false), eq(false), eq(false), eq(false), eq(false), eq(false), eq(true)))
                    .thenReturn(java.util.Arrays.asList("un-upvote"));
            upVotesService.when(() -> CommentUpVotesService.deleteCommentUpVote("comment123"))
                    .thenReturn(null);
            
            // Act
            String result = commentCommands.comment(false, false, false, false, false, false, true, 
                    Optional.of("comment123"), Optional.empty(), Optional.empty(), 1, 5);
            
            // Assert
            assertEquals("You unupvoted a comment with commentId comment123", result);
        }
    }
    
    @Test
    void comment_ListWithException_ShouldReturnErrorMessage() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<CommentsServices> commentsServices = mockStatic(CommentsServices.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(true), eq(false), eq(false), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list"));
            commentsServices.when(() -> CommentsServices.getComments(0, 5, "thread123"))
                    .thenThrow(new IOException("Network error"));
            
            // Act
            String result = commentCommands.comment(true, false, false, false, false, false, false, 
                    Optional.empty(), Optional.empty(), Optional.of("thread123"), 1, 5);
            
            // Assert
            assertEquals("Error retrieving comments: Network error", result);
        }
    }
    
    @Test
    void getAllComments_WithException_ShouldReturnErrorMessage() throws Exception {
        try (MockedStatic<CommentsServices> commentsServices = mockStatic(CommentsServices.class)) {
            // Arrange
            commentsServices.when(() -> CommentsServices.getComments(0, 5, "thread123"))
                    .thenThrow(new IOException("Network error"));
            
            // Act
            String result = CommentCommands.getAllComments(1, 5, "thread123");
            
            // Assert
            assertEquals("Error retrieving comments: Network error", result);
        }
    }
}