package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.entities.CommentUpvote;
import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.exceptions.CommentNotFoundException;
import com.heapoverflow.api.exceptions.CommentUpvoteNotFoundException;
import com.heapoverflow.api.exceptions.UserNotFoundException;
import com.heapoverflow.api.repositories.CommentRepository;
import com.heapoverflow.api.repositories.CommentUpvoteRepository;
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
public class CommentUpvoteServiceTest {

    @Mock
    private CommentUpvoteRepository commentUpvoteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentUpvoteService commentUpvoteService;

    private User testUser;
    private Thread testThread;
    private Comment testComment;
    private CommentUpvote testCommentUpvote;
    private final String authenticatedUserId = "1";

    @BeforeEach
    void setUp() {
        testUser = new User(authenticatedUserId, "John Doe", "john@example.com");
        testThread = new Thread("Test Thread", "Test Description", testUser);
        testThread.setId(1);
        testComment = new Comment("Test Comment", testUser, testThread);
        testComment.setId(1);
        testCommentUpvote = new CommentUpvote(testUser, testComment);
        testCommentUpvote.setId(1);
    }

    @Test
    void createCommentUpvote_Success_ShouldCreateUpvote() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));
            when(commentUpvoteRepository.existsByUserAndComment(testUser, testComment)).thenReturn(false);
            when(commentUpvoteRepository.save(any(CommentUpvote.class))).thenReturn(testCommentUpvote);

            // Act
            CommentUpvote result = commentUpvoteService.createCommentUpvote(1);

            // Assert
            assertNotNull(result);
            assertEquals(testCommentUpvote, result);
            verify(commentUpvoteRepository).save(any(CommentUpvote.class));
        }
    }

    @Test
    void createCommentUpvote_UserNotFound_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> commentUpvoteService.createCommentUpvote(1));
        }
    }

    @Test
    void createCommentUpvote_CommentNotFound_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(commentRepository.findById(1)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(CommentNotFoundException.class, () -> commentUpvoteService.createCommentUpvote(1));
        }
    }

    @Test
    void createCommentUpvote_AlreadyUpvoted_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));
            when(commentUpvoteRepository.existsByUserAndComment(testUser, testComment)).thenReturn(true);

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> commentUpvoteService.createCommentUpvote(1));
        }
    }

    @Test
    void deleteCommentUpvote_Success_ShouldDeleteUpvote() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));
            when(commentUpvoteRepository.findByUserAndComment(testUser, testComment)).thenReturn(Optional.of(testCommentUpvote));
            doNothing().when(commentUpvoteRepository).deleteById(1);

            // Act
            commentUpvoteService.deleteCommentUpvote(1);

            // Assert
            verify(commentUpvoteRepository).deleteById(1);
        }
    }

    @Test
    void deleteCommentUpvote_UserNotFound_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> commentUpvoteService.deleteCommentUpvote(1));
        }
    }

    @Test
    void deleteCommentUpvote_CommentNotFound_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(commentRepository.findById(1)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(CommentNotFoundException.class, () -> commentUpvoteService.deleteCommentUpvote(1));
        }
    }

    @Test
    void deleteCommentUpvote_UpvoteNotFound_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));
            when(commentUpvoteRepository.findByUserAndComment(testUser, testComment)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(CommentUpvoteNotFoundException.class, () -> commentUpvoteService.deleteCommentUpvote(1));
        }
    }
}