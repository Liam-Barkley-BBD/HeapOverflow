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
import org.mockito.Mockito;
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
    private Pageable pageable;
    private final String authenticatedUserId = "1";

    @BeforeEach
    void setUp() {
        testUser = new User(authenticatedUserId, "John Doe", "john@example.com");
        testThread = new Thread("Test Thread", "Test Description", testUser);
        testThread.setId(1);
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getAllThreads_ShouldReturnAllThreads() {
        // Arrange
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread));
        when(threadRepository.findAll(pageable)).thenReturn(threadPage);

        // Act
        Page<Thread> result = threadService.getAllThreads(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testThread, result.getContent().get(0));
        verify(threadRepository).findAll(pageable);
    }

    @Test
    void getThreadById_ExistingThread_ShouldReturnThread() {
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
    void getThreadById_NonExistingThread_ShouldReturnEmpty() {
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
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread));
        when(threadRepository.findByUser_Id(authenticatedUserId, pageable)).thenReturn(threadPage);

        // Act
        Page<Thread> result = threadService.getThreadsByUserId(authenticatedUserId, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testThread, result.getContent().get(0));
        verify(threadRepository).findByUser_Id(authenticatedUserId, pageable);
    }

    @Test
    void getThreadsByFilter_Trending_ShouldReturnTrendingThreads() {
        // Arrange
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread));
        when(threadRepository.findTrendingThreads(any(Pageable.class))).thenReturn(threadPage);

        // Act
        Page<Thread> result = threadService.getThreadsByFilter(true, null, null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(threadRepository).findTrendingThreads(any(Pageable.class));
    }

    @Test
    void getThreadsByFilter_UserThreads_ShouldReturnUserThreads() {
        // Arrange
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread));
        
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(threadRepository.findByUser_Id(authenticatedUserId, pageable)).thenReturn(threadPage);

            // Act
            Page<Thread> result = threadService.getThreadsByFilter(null, true, null, pageable);

            // Assert
            assertEquals(1, result.getTotalElements());
            verify(threadRepository).findByUser_Id(authenticatedUserId, pageable);
        }
    }

    @Test
    void getThreadsByFilter_UserThreads_UserNotFound_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> threadService.getThreadsByFilter(null, true, null, pageable));
        }
    }

    @Test
    void getThreadsByFilter_SearchText_ShouldReturnMatchingThreads() {
        // Arrange
        String searchText = "test";
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread));
        when(threadRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchText, searchText, pageable))
                .thenReturn(threadPage);

        // Act
        Page<Thread> result = threadService.getThreadsByFilter(null, null, searchText, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(threadRepository).findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchText, searchText, pageable);
    }

    @Test
    void getThreadsByFilter_NoFilters_ShouldReturnAllThreads() {
        // Arrange
        Page<Thread> threadPage = new PageImpl<>(Collections.singletonList(testThread));
        when(threadRepository.findAll(pageable)).thenReturn(threadPage);

        // Act
        Page<Thread> result = threadService.getThreadsByFilter(null, null, null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(threadRepository).findAll(pageable);
    }

    @Test
    void createThread_Success_ShouldCreateThread() {
        // Arrange
        ThreadRequest threadRequest = new ThreadRequest();
        threadRequest.setTitle("New Thread");
        threadRequest.setDescription("New Description");

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(threadRepository.save(any(Thread.class))).thenReturn(testThread);

            // Act
            Thread result = threadService.createThread(threadRequest);

            // Assert
            assertNotNull(result);
            assertEquals(testThread, result);
            verify(threadRepository).save(any(Thread.class));
        }
    }

    @Test
    void createThread_UserNotFound_ShouldThrowException() {
        // Arrange
        ThreadRequest threadRequest = new ThreadRequest();
        threadRequest.setTitle("New Thread");
        threadRequest.setDescription("New Description");

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> threadService.createThread(threadRequest));
        }
    }

    @Test
    void updateThread_Success_ShouldUpdateThread() {
        // Arrange
        ThreadUpdate threadUpdate = new ThreadUpdate();
        threadUpdate.setTitle("Updated Title");
        threadUpdate.setDescription("Updated Description");

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));
            when(threadRepository.save(any(Thread.class))).thenReturn(testThread);

            // Act
            Thread result = threadService.updateThread(1, threadUpdate);

            // Assert
            assertNotNull(result);
            verify(threadRepository).save(any(Thread.class));
        }
    }

    @Test
    void updateThread_ThreadNotFound_ShouldThrowException() {
        // Arrange
        ThreadUpdate threadUpdate = new ThreadUpdate();
        threadUpdate.setTitle("Updated Title");

        when(threadRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ThreadNotFoundException.class, () -> threadService.updateThread(999, threadUpdate));
    }

    @Test
    void updateThread_Unauthorized_ShouldThrowException() {
        // Arrange
        ThreadUpdate threadUpdate = new ThreadUpdate();
        threadUpdate.setTitle("Updated Title");

        User differentUser = new User("2", "Jane Doe", "jane@example.com");
        Thread differentUserThread = new Thread("Different Thread", "Different Description", differentUser);
        differentUserThread.setId(1);

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn("2");
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));

            // Act & Assert
            assertThrows(UnauthorizedActionException.class, () -> threadService.updateThread(1, threadUpdate));
        }
    }

    @Test
    void updateThread_ThreadClosed_ShouldThrowException() {
        // Arrange
        ThreadUpdate threadUpdate = new ThreadUpdate();
        threadUpdate.setTitle("Updated Title");

        testThread.setClosedAt(LocalDateTime.now());

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> threadService.updateThread(1, threadUpdate));
        }
    }

    @Test
    void updateThread_WithClosedAt_ShouldUpdateClosedAt() {
        // Arrange
        ThreadUpdate threadUpdate = new ThreadUpdate();
        LocalDateTime closedAt = LocalDateTime.now();
        threadUpdate.setClosedAt(closedAt);

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));
            when(threadRepository.save(any(Thread.class))).thenReturn(testThread);

            // Act
            Thread result = threadService.updateThread(1, threadUpdate);

            // Assert
            assertNotNull(result);
            verify(threadRepository).save(any(Thread.class));
        }
    }

    @Test
    void deleteThread_Success_ShouldDeleteThread() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));
            doNothing().when(threadRepository).deleteById(1);

            // Act
            threadService.deleteThread(1);

            // Assert
            verify(threadRepository).deleteById(1);
        }
    }

    @Test
    void deleteThread_ThreadNotFound_ShouldThrowException() {
        // Arrange
        when(threadRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ThreadNotFoundException.class, () -> threadService.deleteThread(999));
    }

    @Test
    void deleteThread_Unauthorized_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn("2");
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));

            // Act & Assert
            assertThrows(UnauthorizedActionException.class, () -> threadService.deleteThread(1));
        }
    }
}
