package com.heapoverflow.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.models.ThreadRequest;
import com.heapoverflow.api.models.ThreadUpdate;
import com.heapoverflow.api.services.ThreadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ThreadControllerTest {
    @Mock
    private ThreadService threadService;
    
    @InjectMocks
    private ThreadController threadController;
    
    private MockMvc mockMvc;
    private Thread testThread;
    private User testUser;
    private Pageable pageable = PageRequest.of(0, 10);
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
       
        mockMvc = MockMvcBuilders.standaloneSetup(threadController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
               
        testUser = new User("1", "John Doe", "john@example.com");
        testThread = new Thread("Test thread", "How do I do tests using MockMvc?", testUser);
        testThread.setId(1);
        testThread.setCreatedAt(LocalDateTime.now());
    }
    
    /* UNIT Tests */
    
    @Test
    void testGetThreads_WithContent() {
        // Arrange
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread), pageable, 1);
        when(threadService.getThreadsByFilter(isNull(), eq(pageable))).thenReturn(threadPage);
        
        // Act
        ResponseEntity<Page<Thread>> response = threadController.getThreads(null, pageable);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().hasContent());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals("Test thread", response.getBody().getContent().get(0).getTitle());
        verify(threadService).getThreadsByFilter(isNull(), eq(pageable));
    }
    
    @Test
    void testGetThreads_NoContent() {
        // Arrange
        when(threadService.getThreadsByFilter(isNull(), eq(pageable))).thenReturn(Page.empty());
        
        // Act
        ResponseEntity<Page<Thread>> response = threadController.getThreads(null, pageable);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(threadService).getThreadsByFilter(isNull(), eq(pageable));
    }
    
    @Test
    void testGetThreads_WithSearchText() {
        // Arrange
        String searchText = "test";
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread), pageable, 1);
        
        when(threadService.getThreadsByFilter(eq(searchText), eq(pageable))).thenReturn(threadPage);
        
        // Act
        ResponseEntity<Page<Thread>> response = threadController.getThreads(searchText, pageable);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(threadService).getThreadsByFilter(eq(searchText), eq(pageable));
    }
    
    @Test
    void testGetThreadsTrending_WithContent() {
        // Arrange
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread), pageable, 1);
        when(threadService.getThreadsByTrending(pageable)).thenReturn(threadPage);
        
        // Act
        ResponseEntity<Page<Thread>> response = threadController.getThreadsTrending(pageable);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().hasContent());
        assertEquals(1, response.getBody().getTotalElements());
        verify(threadService).getThreadsByTrending(pageable);
    }
    
    @Test
    void testGetThreadsTrending_NoContent() {
        // Arrange
        when(threadService.getThreadsByTrending(pageable)).thenReturn(Page.empty());
        
        // Act
        ResponseEntity<Page<Thread>> response = threadController.getThreadsTrending(pageable);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(threadService).getThreadsByTrending(pageable);
    }
    
    @Test
    void testGetThreadsUser_WithContent() {
        // Arrange
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread), pageable, 1);
        when(threadService.getThreadsForCurrentUser(pageable)).thenReturn(threadPage);
        
        // Act
        ResponseEntity<Page<Thread>> response = threadController.getThreadsUser(pageable);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().hasContent());
        assertEquals(1, response.getBody().getTotalElements());
        verify(threadService).getThreadsForCurrentUser(pageable);
    }
    
    @Test
    void testGetThreadsUser_NoContent() {
        // Arrange
        when(threadService.getThreadsForCurrentUser(pageable)).thenReturn(Page.empty());
        
        // Act
        ResponseEntity<Page<Thread>> response = threadController.getThreadsUser(pageable);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(threadService).getThreadsForCurrentUser(pageable);
    }
    
    @Test
    void testGetThreadsUserWithSearch_WithContent() {
        // Arrange
        String searchText = "test";
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread), pageable, 1);
        when(threadService.getThreadsByFilter(eq(searchText), eq(pageable))).thenReturn(threadPage);
        
        // Act
        ResponseEntity<Page<Thread>> response = threadController.getThreadsUser(searchText, pageable);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().hasContent());
        assertEquals(1, response.getBody().getTotalElements());
        verify(threadService).getThreadsByFilter(eq(searchText), eq(pageable));
    }
    
    @Test
    void testGetThreadById_Success() {
        // Arrange
        Integer threadId = 1;
        when(threadService.getThreadById(threadId)).thenReturn(Optional.of(testThread));
        
        // Act
        ResponseEntity<Thread> response = threadController.getThreadById(threadId);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testThread, response.getBody());
        verify(threadService).getThreadById(threadId);
    }
    
    @Test
    void testGetThreadById_NotFound() {
        // Arrange
        Integer threadId = 999;
        when(threadService.getThreadById(threadId)).thenReturn(Optional.empty());
        
        // Act
        ResponseEntity<Thread> response = threadController.getThreadById(threadId);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(threadService).getThreadById(threadId);
    }
    
    @Test
    void testCreateThread_Success() {
        // Arrange
        ThreadRequest threadRequest = new ThreadRequest();
        threadRequest.setTitle("New Thread");
        threadRequest.setDescription("New thread description");
        
        when(threadService.createThread(any(ThreadRequest.class))).thenReturn(testThread);
        
        // Act
        ResponseEntity<Thread> response = threadController.createThread(threadRequest);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testThread, response.getBody());
        verify(threadService).createThread(any(ThreadRequest.class));
    }
    
    @Test
    void testUpdateThread_Success() {
        // Arrange
        Integer threadId = 1;
        ThreadUpdate threadUpdate = new ThreadUpdate();
        threadUpdate.setTitle("Updated Title");
        threadUpdate.setDescription("Updated description");
        
        Thread updatedThread = new Thread("Updated Title", "Updated description", testUser);
        updatedThread.setId(threadId);
        
        when(threadService.updateThread(eq(threadId), any(ThreadUpdate.class))).thenReturn(updatedThread);
        
        // Act
        ResponseEntity<Thread> response = threadController.updateThread(threadId, threadUpdate);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedThread, response.getBody());
        verify(threadService).updateThread(eq(threadId), any(ThreadUpdate.class));
    }
    
    @Test
    void testDeleteThread_Success() {
        // Arrange
        Integer threadId = 1;
        doNothing().when(threadService).deleteThread(threadId);
        
        // Act
        ResponseEntity<Void> response = threadController.deleteThread(threadId);
        
        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(threadService).deleteThread(threadId);
    }

}