package com.heapoverflow.api.controllers;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ThreadControllerTest {

    @Mock
    private ThreadService threadService;

    @InjectMocks
    private ThreadController threadController;

    private Thread testThread;
    private User testUser;
    private Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
                
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
        when(threadService.getThreadsByFilter(null, null, null, pageable)).thenReturn(threadPage);

        // Act
        ResponseEntity<Page<Thread>> response = threadController.getThreads(null, null, null, pageable);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().hasContent());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(testThread, response.getBody().getContent().get(0));
        verify(threadService).getThreadsByFilter(null, null, null, pageable);
    }

    @Test
    void testGetThreads_NoContent() {
        // Arrange
        when(threadService.getThreadsByFilter(null, null, null, pageable)).thenReturn(Page.empty());

        // Act
        ResponseEntity<Page<Thread>> response = threadController.getThreads(null, null, null, pageable);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(threadService).getThreadsByFilter(null, null, null, pageable);
    }

    @Test
    void testGetThreads_WithFilters() {
        // Arrange
        Boolean isTrending = true;
        Boolean userThreads = true;
        String searchText = "test";
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread), pageable, 1);
        
        when(threadService.getThreadsByFilter(isTrending, userThreads, searchText, pageable)).thenReturn(threadPage);
        
        // Act
        ResponseEntity<Page<Thread>> response = threadController.getThreads(isTrending, userThreads, searchText, pageable);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(threadService).getThreadsByFilter(isTrending, userThreads, searchText, pageable);
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