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
import com.heapoverflow.cli.services.ThreadsService;
import com.heapoverflow.cli.services.ThreadUpvoteServices;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.FlagsCheckUtils;
import com.heapoverflow.cli.utils.TextUtils;

@ExtendWith(MockitoExtension.class)
public class ThreadCommandsTests {

    @InjectMocks
    private ThreadCommands threadCommands;

    private ObjectMapper objectMapper;
    private JsonNode singleThreadNode;
    private JsonNode threadsListNode;
    private JsonNode emptyThreadsListNode;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        
        // Create a single thread node
        singleThreadNode = createThreadNode("thread123", "Test Thread", "Thread description", "user123", 
                "testuser", "test@example.com", "2025-03-15T10:00:00Z", null, 5);
        
        // Create a list of threads
        ArrayNode threadsArray = objectMapper.createArrayNode();
        threadsArray.add(createThreadNode("thread1", "First Thread", "First description", "user1", 
                "user1", "user1@example.com", "2025-03-15T10:00:00Z", null, 10));
        threadsArray.add(createThreadNode("thread2", "Second Thread", "Second description", "user2", 
                "user2", "user2@example.com", "2025-03-15T11:00:00Z", "2025-03-16T11:00:00Z", 5));
        
        // Create threads list response
        ObjectNode threadsResponseNode = objectMapper.createObjectNode();
        threadsResponseNode.set("content", threadsArray);
        threadsResponseNode.put("totalElements", 2);
        threadsResponseNode.put("totalPages", 1);
        threadsResponseNode.put("number", 0);
        threadsResponseNode.put("last", true);
        threadsListNode = threadsResponseNode;
        
        // Create empty threads list response
        ObjectNode emptyThreadsResponseNode = objectMapper.createObjectNode();
        emptyThreadsResponseNode.set("content", objectMapper.createArrayNode());
        emptyThreadsResponseNode.put("totalElements", 0);
        emptyThreadsResponseNode.put("totalPages", 0);
        emptyThreadsResponseNode.put("number", 0);
        emptyThreadsResponseNode.put("last", true);
        emptyThreadsListNode = emptyThreadsResponseNode;
    }

    private JsonNode createThreadNode(String id, String title, String description, String userId, 
            String username, String email, String createdAt, String closedAt, int upvotesCount) {
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("id", userId);
        userNode.put("username", username);
        userNode.put("email", email);
        
        ObjectNode threadNode = objectMapper.createObjectNode();
        threadNode.put("id", id);
        threadNode.put("title", title);
        threadNode.put("description", description);
        threadNode.put("createdAt", createdAt);
        if (closedAt != null) {
            threadNode.put("closedAt", closedAt);
        } else {
            threadNode.putNull("closedAt");
        }
        threadNode.put("threadUpvotesCount", upvotesCount);
        threadNode.set("user", userNode);
        
        return threadNode;
    }

    @Test
    void thread_NotLoggedIn_ShouldReturnLoginMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(false);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(true), eq(false), eq(false), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list"));
            
            // Act
            String result = threadCommands.thread(true, false, false, false, false, false, false, 
                    false, false, false, Optional.empty(), Optional.empty(), Optional.empty(), 
                    Optional.empty(), 1, 10);
            
            // Assert
            assertEquals("You are not logged in. Please log in!", result);
        }
    }

    @Test
    void thread_MultipleFlags_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(true), eq(true), eq(false), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list", "get"));
            
            // Act
            String result = threadCommands.thread(true, false, false, true, false, false, false, 
                    false, false, false, Optional.empty(), Optional.empty(), Optional.empty(), 
                    Optional.empty(), 1, 10);
            
            // Assert
            assertTrue(result.startsWith("You cannot use multiple action based flags at once:"));
            assertTrue(result.contains("list") && result.contains("get"));
        }
    }

    @Test
    void thread_NoOptionSelected_ShouldReturnHelpMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(false), eq(false), eq(false), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Collections.emptyList());
            
            // Act
            String result = threadCommands.thread(false, false, false, false, false, false, false, 
                    false, false, false, Optional.empty(), Optional.empty(), Optional.empty(), 
                    Optional.empty(), 1, 10);
            
            // Assert
            assertTrue(result.startsWith("Invalid command. Use:"));
        }
    }

    @Test
    void thread_ListWithNoThreads_ShouldReturnEmptyThreadsTable() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<ThreadsService> threadsService = mockStatic(ThreadsService.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(true), eq(false), eq(false), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list"));
            threadsService.when(() -> ThreadsService.getThreads("", 0, 10))
                    .thenReturn(emptyThreadsListNode);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Empty Thread Table");
            
            // Act
            String result = threadCommands.thread(true, false, false, false, false, false, false, 
                    false, false, false, Optional.empty(), Optional.empty(), Optional.empty(), 
                    Optional.empty(), 1, 10);
            
            // Assert
            assertTrue(result.startsWith("Empty Thread Table"));
            assertTrue(result.contains("Page 1 of 0 | Total Threads: 0 (Last Page)"));
        }
    }

    @Test
    void thread_ListWithThreads_ShouldReturnThreadsTable() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<ThreadsService> threadsService = mockStatic(ThreadsService.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(true), eq(false), eq(false), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("list"));
            threadsService.when(() -> ThreadsService.getThreads("test", 0, 10))
                    .thenReturn(threadsListNode);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered Threads Table");
            
            // Act
            String result = threadCommands.thread(true, false, false, false, false, false, false, 
                    false, false, false, Optional.of("test"), Optional.empty(), Optional.empty(), 
                    Optional.empty(), 1, 10);
            
            // Assert
            assertTrue(result.startsWith("Rendered Threads Table"));
            assertTrue(result.contains("Page 1 of 1 | Total Threads: 2 (Last Page)"));
        }
    }

    @Test
    void thread_PostWithValidData_ShouldReturnNewThread() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<ThreadsService> threadsService = mockStatic(ThreadsService.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(false), eq(false), eq(true), eq(false), eq(false), eq(false), eq(false)))
                    .thenReturn(java.util.Arrays.asList("post"));
            threadsService.when(() -> ThreadsService.postThread("Test Title", "Test Description"))
                    .thenReturn(singleThreadNode);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered New Thread Table");
            
            // Act
            String result = threadCommands.thread(false, false, false, false, true, false, false, 
                    false, false, false, Optional.empty(), Optional.empty(), 
                    Optional.of("Test Title"), Optional.of("Test Description"), 1, 10);
            
            // Assert
            assertTrue(result.startsWith("Thread created successfully:"));
            assertTrue(result.contains("Rendered New Thread Table"));
        }
    }


    @Test
    void thread_UpvoteWithValidId_ShouldReturnSuccessMessage() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<FlagsCheckUtils> flagsCheckUtils = mockStatic(FlagsCheckUtils.class);
             MockedStatic<ThreadUpvoteServices> upvoteServices = mockStatic(ThreadUpvoteServices.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)).thenReturn(true);
            flagsCheckUtils.when(() -> FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(
                    eq(false), eq(false), eq(false), eq(false), eq(false), eq(true), eq(false)))
                    .thenReturn(java.util.Arrays.asList("upvote"));
            upvoteServices.when(() -> ThreadUpvoteServices.postThreadUpVote(123))
                    .thenReturn(null);
            
            // Act
            String result = threadCommands.thread(false, false, false, false, false, false, false, 
                    true, false, false, Optional.empty(), Optional.of("123"), Optional.empty(), 
                    Optional.empty(), 1, 10);
            
            // Assert
            assertEquals("Successfully upvoted thread ID: 123", result);
        }
    }
}