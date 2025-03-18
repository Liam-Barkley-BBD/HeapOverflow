package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.exceptions.ThreadNotFoundException;
import com.heapoverflow.api.exceptions.UnauthorizedActionException;
import com.heapoverflow.api.exceptions.UserNotFoundException;
import com.heapoverflow.api.models.ThreadRequest;
import com.heapoverflow.api.models.ThreadUpdate;
import com.heapoverflow.api.repositories.ThreadRepository;
import com.heapoverflow.api.repositories.UserRepository;
import com.heapoverflow.api.utils.AuthUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ThreadServiceTest {

    @Mock
    private ThreadRepository threadRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ThreadService threadService;

    private User testUser;
    private Thread testThread;
    private ThreadRequest testThreadRequest;
    private ThreadUpdate testThreadUpdate;
    private Pageable pageable;
    private final String authenticatedUserId = "user123";

    @BeforeEach
    void setUp() {
        testUser = new User(authenticatedUserId, "Test User", "test@example.com");
        testThread = new Thread("Test Thread", "Test Description", testUser);
        testThread.setId(1);

        testThreadRequest = new ThreadRequest();
        testThreadRequest.setTitle("New Thread");
        testThreadRequest.setDescription("New Description");

        testThreadUpdate = new ThreadUpdate();
        testThreadUpdate.setTitle("Updated Title");
        testThreadUpdate.setDescription("Updated Description");

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getAllThreads_ShouldReturnAllThreads() {
        // Arrange
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread), pageable, 1);
        when(threadRepository.findAll(pageable)).thenReturn(threadPage);

        // Act
        Page<Thread> result = threadService.getAllThreads(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testThread, result.getContent().get(0));
        verify(threadRepository).findAll(pageable);
    }

    @Test
    void getThreadById_WithExistingId_ShouldReturnThread() {
        // Arrange
        when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));

        // Act
        Optional<Thread> result = threadService.getThreadById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testThread, result.get());
        verify(threadRepository).findById(1);
    }

    @Test
    void getThreadById_WithNonExistingId_ShouldReturnEmptyOptional() {
        // Arrange
        when(threadRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Thread> result = threadService.getThreadById(999);

        // Assert
        assertFalse(result.isPresent());
        verify(threadRepository).findById(999);
    }

    @Test
    void getThreadsByUserId_ShouldReturnUserThreads() {
        // Arrange
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread), pageable, 1);
        when(threadRepository.findByUser_Id(authenticatedUserId, pageable)).thenReturn(threadPage);

        // Act
        Page<Thread> result = threadService.getThreadsByUserId(authenticatedUserId, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testThread, result.getContent().get(0));
        verify(threadRepository).findByUser_Id(authenticatedUserId, pageable);
    }

    @Test
    void getThreadsByFilter_WithSearchText_ShouldReturnMatchingThreads() {
        // Arrange
        String searchText = "Test";
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread), pageable, 1);
        when(threadRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchText, searchText, pageable))
                .thenReturn(threadPage);

        // Act
        Page<Thread> result = threadService.getThreadsByFilter(searchText, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testThread, result.getContent().get(0));
        verify(threadRepository).findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchText, searchText, pageable);
    }

    @Test
    void getThreadsByFilter_WithNullSearchText_ShouldReturnAllThreads() {
        // Arrange
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread), pageable, 1);
        when(threadRepository.findAll(pageable)).thenReturn(threadPage);

        // Act
        Page<Thread> result = threadService.getThreadsByFilter(null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testThread, result.getContent().get(0));
        verify(threadRepository).findAll(pageable);
    }

    @Test
    void getThreadsByTrending_ShouldReturnTrendingThreads() {
        // Arrange
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread), pageable, 1);
        when(threadRepository.findTrendingThreads(any(Pageable.class))).thenReturn(threadPage);

        // Act
        Page<Thread> result = threadService.getThreadsByTrending(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testThread, result.getContent().get(0));
        verify(threadRepository).findTrendingThreads(any(Pageable.class));
    }

    @Test
    void getThreadsForCurrentUser_ShouldReturnCurrentUserThreads() {
        // Arrange
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread), pageable, 1);
        
        try (MockedStatic<AuthUtils> authUtils = mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(threadRepository.findByUser_Id(authenticatedUserId, pageable)).thenReturn(threadPage);

            // Act
            Page<Thread> result = threadService.getThreadsForCurrentUser(pageable);

            // Assert
            assertEquals(1, result.getTotalElements());
            assertEquals(testThread, result.getContent().get(0));
            verify(threadRepository).findByUser_Id(authenticatedUserId, pageable);
            verify(userRepository).findById(authenticatedUserId);
        }
    }

    @Test
    void getThreadsForCurrentUser_UserNotFound_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> threadService.getThreadsForCurrentUser(pageable));
            verify(userRepository).findById(authenticatedUserId);
            verify(threadRepository, never()).findByUser_Id(anyString(), any(Pageable.class));
        }
    }

    @Test
    void createThread_Success() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(threadRepository.save(any(Thread.class))).thenAnswer(invocation -> {
                Thread savedThread = invocation.getArgument(0);
                savedThread.setId(1);
                return savedThread;
            });

            // Act
            Thread result = threadService.createThread(testThreadRequest);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getId());
            assertEquals(testThreadRequest.getTitle(), result.getTitle());
            assertEquals(testThreadRequest.getDescription(), result.getDescription());
            assertEquals(testUser, result.getUser());
            verify(userRepository).findById(authenticatedUserId);
            verify(threadRepository).save(any(Thread.class));
        }
    }

    @Test
    void createThread_UserNotFound_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> threadService.createThread(testThreadRequest));
            verify(userRepository).findById(authenticatedUserId);
            verify(threadRepository, never()).save(any(Thread.class));
        }
    }

    @Test
    void updateThread_Success() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));
            when(threadRepository.save(any(Thread.class))).thenReturn(testThread);

            // Act
            Thread result = threadService.updateThread(1, testThreadUpdate);

            // Assert
            assertNotNull(result);
            assertEquals(testThreadUpdate.getTitle(), result.getTitle());
            assertEquals(testThreadUpdate.getDescription(), result.getDescription());
            verify(threadRepository).findById(1);
            verify(threadRepository).save(testThread);
        }
    }

    @Test
    void updateThread_ThreadNotFound_ShouldThrowException() {
        // Arrange
        when(threadRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ThreadNotFoundException.class, () -> threadService.updateThread(999, testThreadUpdate));
        verify(threadRepository).findById(999);
        verify(threadRepository, never()).save(any(Thread.class));
    }

    @Test
    void updateThread_NotAuthorized_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn("differentUser");
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));

            // Act & Assert
            assertThrows(UnauthorizedActionException.class, () -> threadService.updateThread(1, testThreadUpdate));
            verify(threadRepository).findById(1);
            verify(threadRepository, never()).save(any(Thread.class));
        }
    }

    @Test
    void updateThread_ThreadClosed_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            
            testThread.setClosedAt(LocalDateTime.now());
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> threadService.updateThread(1, testThreadUpdate));
            verify(threadRepository).findById(1);
            verify(threadRepository, never()).save(any(Thread.class));
        }
    }

    @Test
    void updateThread_WithClosedAtField_ShouldUpdateClosedAt() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));
            when(threadRepository.save(any(Thread.class))).thenReturn(testThread);

            LocalDateTime closedAt = LocalDateTime.now();
            testThreadUpdate.setClosedAt(closedAt);

            // Act
            Thread result = threadService.updateThread(1, testThreadUpdate);

            // Assert
            assertEquals(closedAt, result.getClosedAt());
            verify(threadRepository).save(testThread);
        }
    }

    @Test
    void deleteThread_Success() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));
            doNothing().when(threadRepository).deleteById(1);

            // Act
            threadService.deleteThread(1);

            // Assert
            verify(threadRepository).findById(1);
            verify(threadRepository).deleteById(1);
        }
    }

    @Test
    void deleteThread_ThreadNotFound_ShouldThrowException() {
        // Arrange
        when(threadRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ThreadNotFoundException.class, () -> threadService.deleteThread(999));
        verify(threadRepository).findById(999);
        verify(threadRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteThread_NotAuthorized_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn("differentUser");
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));

            // Act & Assert
            assertThrows(UnauthorizedActionException.class, () -> threadService.deleteThread(1));
            verify(threadRepository).findById(1);
            verify(threadRepository, never()).deleteById(anyInt());
        }
    }
}