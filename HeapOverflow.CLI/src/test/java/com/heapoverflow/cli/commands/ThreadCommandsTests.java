package com.heapoverflow.cli.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.shell.table.TableModel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.ThreadUpvoteServices;
import com.heapoverflow.cli.services.ThreadsService;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.TextUtils;

@ExtendWith(MockitoExtension.class)
public class ThreadCommandsTests {

    @InjectMocks
    private ThreadCommands threadCommands;

    @Mock
    private CommentCommands commentCommands;

    private ObjectMapper objectMapper;
    private ObjectNode mockThreadNode;
    private ObjectNode mockPageResponse;
    private ArrayNode mockThreadArray;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        
        // Create a mock thread
        mockThreadNode = objectMapper.createObjectNode();
        mockThreadNode.put("id", "123");
        mockThreadNode.put("title", "Test Thread");
        mockThreadNode.put("description", "Test Description");
        mockThreadNode.put("createdAt", "2023-01-01T12:00:00Z");
        mockThreadNode.putNull("closedAt");
        mockThreadNode.put("threadUpvotesCount", 5);
        
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("username", "testUser");
        mockThreadNode.set("user", userNode);
        
        // Create a mock array of threads
        mockThreadArray = objectMapper.createArrayNode();
        mockThreadArray.add(mockThreadNode);
        
        // Create a mock page response
        mockPageResponse = objectMapper.createObjectNode();
        mockPageResponse.set("content", mockThreadArray);
        mockPageResponse.put("totalElements", 1);
        mockPageResponse.put("totalPages", 1);
        mockPageResponse.put("number", 0);
        mockPageResponse.put("last", true);
    }

    @Test
    void thread_WhenNotLoggedIn_ShouldReturnLoginMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(false);
            
            // Act
            String result = threadCommands.thread(false, false, false, false, 
                    false, false, false, false, false, "", 
                    false, "", "", "", 1, 10);
            
            // Assert
            assertEquals("You are not logged in. Please log in!", result);
        }
    }

    @Test
    void thread_WithInvalidCommand_ShouldReturnHelpMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            
            // Act
            String result = threadCommands.thread(false, false, false, false, 
                    false, false, false, false, false, "", 
                    false, "", "", "", 1, 10);
            
            // Assert
            assertTrue(result.startsWith("Invalid command. Use:"));
        }
    }
    
    @Test
    void thread_WithListOption_ShouldReturnThreadList() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<ThreadsService> threadsService = mockStatic(ThreadsService.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            threadsService.when(() -> ThreadsService.getThreads(anyString(), anyInt(), anyInt()))
                    .thenReturn(mockPageResponse);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered Table");
            
            // Act
            String result = threadCommands.thread(true, false, false, false, 
                    false, false, false, false, false, "", 
                    false, "", "", "", 1, 10);
            
            // Assert
            assertTrue(result.contains("Rendered Table"));
            assertTrue(result.contains("Page 1 of 1"));
            threadsService.verify(() -> ThreadsService.getThreads(anyString(), eq(0), eq(10)));
        }
    }
    
    @Test
    void thread_WithTrendingOption_ShouldReturnTrendingThreads() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<ThreadsService> threadsService = mockStatic(ThreadsService.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            threadsService.when(() -> ThreadsService.getThreadsTrending())
                    .thenReturn(mockPageResponse);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered Table");
            
            // Act
            String result = threadCommands.thread(false, true, false, false, 
                    false, false, false, false, false, "", 
                    false, "", "", "", 1, 10);
            
            // Assert
            assertTrue(result.contains("Rendered Table"));
            threadsService.verify(() -> ThreadsService.getThreadsTrending());
        }
    }
    
    @Test
    void thread_WithUserThreadsOption_ShouldReturnUserThreads() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<ThreadsService> threadsService = mockStatic(ThreadsService.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            threadsService.when(() -> ThreadsService.getThreadsByUser())
                    .thenReturn(mockPageResponse);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered Table");
            
            // Act
            String result = threadCommands.thread(false, false, true, false, 
                    false, false, false, false, false, "", 
                    false, "", "", "", 1, 10);
            
            // Assert
            assertTrue(result.contains("Rendered Table"));
            threadsService.verify(() -> ThreadsService.getThreadsByUser());
        }
    }
    
    @Test
    void thread_WithGetOption_ShouldReturnSpecificThread() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<ThreadsService> threadsService = mockStatic(ThreadsService.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class);
             MockedStatic<CommentCommands> commentCommands = mockStatic(CommentCommands.class)) {
            
            // Arrange
            String threadId = "123";
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            threadsService.when(() -> ThreadsService.getThreadsById(threadId))
                    .thenReturn(mockThreadNode);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered Thread Table");
            commentCommands.when(() -> CommentCommands.getAllComments(anyInt(), anyInt(), eq(threadId)))
                    .thenReturn("Rendered Comments");
            
            // Act
            String result = threadCommands.thread(false, false, false, true, 
                    false, false, false, false, false, "", 
                    false, threadId, "", "", 1, 10);
            
            // Assert
            assertTrue(result.contains("Rendered Thread Table"));
            assertTrue(result.contains("Rendered Comments"));
            threadsService.verify(() -> ThreadsService.getThreadsById(threadId));
        }
    }
    
    @Test
    void thread_WithPostOption_ShouldCreateNewThread() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<ThreadsService> threadsService = mockStatic(ThreadsService.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            String title = "New Thread";
            String description = "New Description";
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            threadsService.when(() -> ThreadsService.postThread(title, description))
                    .thenReturn(mockThreadNode);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered Table");
            
            // Act
            String result = threadCommands.thread(false, false, false, false, 
                    true, false, false, false, false, "", 
                    false, "", title, description, 1, 10);
            
            // Assert
            assertTrue(result.contains("Thread created successfully"));
            assertTrue(result.contains("Rendered Table"));
            threadsService.verify(() -> ThreadsService.postThread(title, description));
        }
    }
    
    @Test
    void thread_WithEditOptionAndEmptyThreadId_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            
            // Act
            String result = threadCommands.thread(false, false, false, false, 
                    false, true, false, false, false, "", 
                    false, "", "Updated Title", "Updated Description", 1, 10);
            
            // Assert
            assertEquals("ThreadId should be initialized eg thread --edit --threadId {id}", result);
        }
    }
    
    @Test
    void thread_WithEditOption_ShouldUpdateThread() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<ThreadsService> threadsService = mockStatic(ThreadsService.class);
             MockedStatic<TextUtils> textUtils = mockStatic(TextUtils.class)) {
            
            // Arrange
            String threadId = "123";
            String title = "Updated Title";
            String description = "Updated Description";
            boolean closeThread = true;
            
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            threadsService.when(() -> ThreadsService.patchThread(threadId, title, description, closeThread))
                    .thenReturn(mockThreadNode);
            textUtils.when(() -> TextUtils.renderTable(any(TableModel.class)))
                    .thenReturn("Rendered Table");
            
            // Act
            String result = threadCommands.thread(false, false, false, false, 
                    false, true, false, false, false, "", 
                    closeThread, threadId, title, description, 1, 10);
            
            // Assert
            assertEquals("Rendered Table", result);
            threadsService.verify(() -> ThreadsService.patchThread(threadId, title, description, closeThread));
        }
    }
    
    @Test
    void thread_WithDeleteOption_ShouldDeleteThread() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<ThreadsService> threadsService = mockStatic(ThreadsService.class)) {
            
            // Arrange
            String threadId = "123";
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            
            // Act
            String result = threadCommands.thread(false, false, false, false, 
                    false, false, true, false, false, "", 
                    false, threadId, "", "", 1, 10);
            
            // Assert
            assertEquals("Thread deleted successfully.", result);
            threadsService.verify(() -> ThreadsService.deleteThread(threadId));
        }
    }
    
    @Test
    void thread_WithUpvoteOptionAndEmptyThreadId_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            
            // Act
            String result = threadCommands.thread(false, false, false, false, 
                    false, false, false, true, false, "", 
                    false, "", "", "", 1, 10);
            
            // Assert
            assertTrue(result.contains("Thread ID must be specified"));
        }
    }
    
    @Test
    void thread_WithUpvoteOption_ShouldUpvoteThread() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<ThreadUpvoteServices> threadUpvoteServices = mockStatic(ThreadUpvoteServices.class)) {
            
            // Arrange
            String threadId = "123";
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            
            // Act
            String result = threadCommands.thread(false, false, false, false, 
                    false, false, false, true, false, "", 
                    false, threadId, "", "", 1, 10);
            
            // Assert
            assertTrue(result.contains("Successfully upvoted thread ID"));
            threadUpvoteServices.verify(() -> ThreadUpvoteServices.postThreadUpVote(Integer.parseInt(threadId)));
        }
    }
    
    @Test
    void thread_WithRemoveUpvoteOptionAndEmptyThreadId_ShouldReturnErrorMessage() {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class)) {
            // Arrange
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            
            // Act
            String result = threadCommands.thread(false, false, false, false, 
                    false, false, false, false, true, "", 
                    false, "", "", "", 1, 10);
            
            // Assert
            assertTrue(result.contains("Thread Id must be specified"));
        }
    }
    
    @Test
    void thread_WithRemoveUpvoteOption_ShouldRemoveUpvote() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<ThreadUpvoteServices> threadUpvoteServices = mockStatic(ThreadUpvoteServices.class)) {
            
            // Arrange
            String threadId = "123";
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            
            // Act
            String result = threadCommands.thread(false, false, false, false, 
                    false, false, false, false, true, "", 
                    false, threadId, "", "", 1, 10);
            
            // Assert
            assertTrue(result.contains("Successfully removed upvote"));
            threadUpvoteServices.verify(() -> ThreadUpvoteServices.deleteThreadUpVote(threadId));
        }
    }
    
    @Test
    void thread_WithListOption_WhenExceptionThrown_ShouldReturnErrorMessage() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<ThreadsService> threadsService = mockStatic(ThreadsService.class)) {
            
            // Arrange
            Exception expectedException = new IOException("Network error");
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            threadsService.when(() -> ThreadsService.getThreads(anyString(), anyInt(), anyInt()))
                    .thenThrow(expectedException);
            
            // Act
            String result = threadCommands.thread(true, false, false, false, 
                    false, false, false, false, false, "", 
                    false, "", "", "", 1, 10);
            
            // Assert
            assertEquals("Error retrieving threads: Network error", result);
        }
    }
    
    @Test
    void thread_WithListOption_WhenNoThreadsFound_ShouldReturnAppropriateMessage() throws Exception {
        try (MockedStatic<EnvUtils> envUtils = mockStatic(EnvUtils.class);
             MockedStatic<ThreadsService> threadsService = mockStatic(ThreadsService.class)) {
            
            // Arrange
            ObjectNode emptyResponse = objectMapper.createObjectNode();
            ArrayNode emptyArray = objectMapper.createArrayNode();
            emptyResponse.set("content", emptyArray);
            
            envUtils.when(() -> EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN))
                    .thenReturn(true);
            threadsService.when(() -> ThreadsService.getThreads(anyString(), anyInt(), anyInt()))
                    .thenReturn(emptyResponse);
            
            // Act
            String result = threadCommands.thread(true, false, false, false, 
                    false, false, false, false, false, "", 
                    false, "", "", "", 1, 10);
            
            // Assert
            assertEquals("No threads found.", result);
        }
    }
}