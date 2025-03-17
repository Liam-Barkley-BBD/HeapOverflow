package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.entities.ThreadUpvote;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.exceptions.ThreadNotFoundException;
import com.heapoverflow.api.exceptions.UserNotFoundException;
import com.heapoverflow.api.repositories.ThreadRepository;
import com.heapoverflow.api.repositories.ThreadUpvoteRepository;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ThreadUpvoteServiceTest {

    @Mock
    private ThreadUpvoteRepository threadUpvoteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ThreadRepository threadRepository;

    @InjectMocks
    private ThreadUpvoteService threadUpvoteService;

    private User testUser;
    private Thread testThread;
    private ThreadUpvote testThreadUpvote;
    private Pageable pageable;
    private final String authenticatedUserId = "1";

    @BeforeEach
    void setUp() {
        testUser = new User(authenticatedUserId, "John Doe", "john@example.com");
        testThread = new Thread("Test Thread", "Test Description", testUser);
        testThread.setId(1);
        testThreadUpvote = new ThreadUpvote(testUser, testThread);
        testThreadUpvote.setId(1);
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getAllThreadUpvotes_ShouldReturnAllThreadUpvotes() {
        // Arrange
        Page<ThreadUpvote> threadUpvotePage = new PageImpl<>(Collections.singletonList(testThreadUpvote));
        when(threadUpvoteRepository.findAll(pageable)).thenReturn(threadUpvotePage);

        // Act
        Page<ThreadUpvote> result = threadUpvoteService.getAllThreadUpvotes(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testThreadUpvote, result.getContent().get(0));
        verify(threadUpvoteRepository).findAll(pageable);
    }

    @Test
    void getThreadUpvoteById_ExistingUpvote_ShouldReturnUpvote() {
        // Arrange
        when(threadUpvoteRepository.findById(1)).thenReturn(Optional.of(testThreadUpvote));

        // Act
        Optional<ThreadUpvote> result = threadUpvoteService.getThreadUpvoteById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testThreadUpvote, result.get());
        verify(threadUpvoteRepository).findById(1);
    }

    @Test
    void getThreadUpvoteById_NonExistingUpvote_ShouldReturnEmpty() {
        // Arrange
        when(threadUpvoteRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<ThreadUpvote> result = threadUpvoteService.getThreadUpvoteById(999);

        // Assert
        assertFalse(result.isPresent());
        verify(threadUpvoteRepository).findById(999);
    }

    @Test
    void getThreadUpvotesByFilter_ByUserId_ShouldReturnUserUpvotes() {
        // Arrange
        Page<ThreadUpvote> threadUpvotePage = new PageImpl<>(Collections.singletonList(testThreadUpvote));
        when(threadUpvoteRepository.findByUser_Id(authenticatedUserId, pageable)).thenReturn(threadUpvotePage);

        // Act
        Page<ThreadUpvote> result = threadUpvoteService.getThreadUpvotesByFilter(authenticatedUserId, null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testThreadUpvote, result.getContent().get(0));
        verify(threadUpvoteRepository).findByUser_Id(authenticatedUserId, pageable);
    }

    @Test
    void getThreadUpvotesByFilter_ByThreadId_ShouldReturnThreadUpvotes() {
        // Arrange
        Page<ThreadUpvote> threadUpvotePage = new PageImpl<>(Collections.singletonList(testThreadUpvote));
        when(threadUpvoteRepository.findByThread_Id(1, pageable)).thenReturn(threadUpvotePage);

        // Act
        Page<ThreadUpvote> result = threadUpvoteService.getThreadUpvotesByFilter(null, 1, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testThreadUpvote, result.getContent().get(0));
        verify(threadUpvoteRepository).findByThread_Id(1, pageable);
    }

    @Test
    void getThreadUpvotesByFilter_NoFilters_ShouldReturnAllUpvotes() {
        // Arrange
        Page<ThreadUpvote> threadUpvotePage = new PageImpl<>(Collections.singletonList(testThreadUpvote));
        when(threadUpvoteRepository.findAll(pageable)).thenReturn(threadUpvotePage);

        // Act
        Page<ThreadUpvote> result = threadUpvoteService.getThreadUpvotesByFilter(null, null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testThreadUpvote, result.getContent().get(0));
        verify(threadUpvoteRepository).findAll(pageable);
    }

    @Test
    void createThreadUpvote_Success_ShouldCreateUpvote() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));
            when(threadUpvoteRepository.existsByUserAndThread(testUser, testThread)).thenReturn(false);
            when(threadUpvoteRepository.save(any(ThreadUpvote.class))).thenReturn(testThreadUpvote);

            // Act
            ThreadUpvote result = threadUpvoteService.createThreadUpvote(1);

            // Assert
            assertNotNull(result);
            assertEquals(testThreadUpvote, result);
            verify(threadUpvoteRepository).save(any(ThreadUpvote.class));
        }
    }

    @Test
    void createThreadUpvote_UserNotFound_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> threadUpvoteService.createThreadUpvote(1));
        }
    }

    @Test
    void createThreadUpvote_ThreadNotFound_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(threadRepository.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ThreadNotFoundException.class, () -> threadUpvoteService.createThreadUpvote(999));
        }
    }

    @Test
    void createThreadUpvote_AlreadyUpvoted_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));
            when(threadUpvoteRepository.existsByUserAndThread(testUser, testThread)).thenReturn(true);

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> threadUpvoteService.createThreadUpvote(1));
        }
    }

    @Test
    void deleteThreadUpvote_Success_ShouldDeleteUpvote() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));
            when(threadUpvoteRepository.findByUserAndThread(testUser, testThread)).thenReturn(Optional.of(testThreadUpvote));
            doNothing().when(threadUpvoteRepository).deleteById(1);

            // Act
            threadUpvoteService.deleteThreadUpvote(1);

            // Assert
            verify(threadUpvoteRepository).deleteById(1);
        }
    }

    @Test
    void deleteThreadUpvote_UserNotFound_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> threadUpvoteService.deleteThreadUpvote(1));
        }
    }

    @Test
    void deleteThreadUpvote_ThreadNotFound_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(threadRepository.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ThreadNotFoundException.class, () -> threadUpvoteService.deleteThreadUpvote(999));
        }
    }

    @Test
    void deleteThreadUpvote_UpvoteNotFound_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));
            when(threadUpvoteRepository.findByUserAndThread(testUser, testThread)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ThreadNotFoundException.class, () -> threadUpvoteService.deleteThreadUpvote(1));
        }
    }
}