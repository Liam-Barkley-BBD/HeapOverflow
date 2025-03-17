package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.exceptions.CommentNotFoundException;
import com.heapoverflow.api.exceptions.ReplyNotFoundException;
import com.heapoverflow.api.exceptions.ThreadNotFoundException;
import com.heapoverflow.api.exceptions.UnauthorizedActionException;
import com.heapoverflow.api.exceptions.UserNotFoundException;
import com.heapoverflow.api.models.CommentRequest;
import com.heapoverflow.api.repositories.CommentRepository;
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
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ThreadRepository threadRepository;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private Thread testThread;
    private Comment testComment;
    private Pageable pageable;
    private final String authenticatedUserId = "1";

    @BeforeEach
    void setUp() {
        testUser = new User(authenticatedUserId, "John Doe", "john@example.com");
        testThread = new Thread("Test Thread", "Test Description", testUser);
        testThread.setId(1);
        testComment = new Comment("Test Comment", testUser, testThread);
        testComment.setId(1);
        testComment.setCreatedAt(LocalDateTime.now());
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getAllComments_ShouldReturnAllComments() {
        // Arrange
        Page<Comment> commentPage = new PageImpl<>(Collections.singletonList(testComment));
        when(commentRepository.findAll(pageable)).thenReturn(commentPage);

        // Act
        Page<Comment> result = commentService.getAllComments(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testComment, result.getContent().get(0));
        verify(commentRepository).findAll(pageable);
    }

    @Test
    void getCommentById_ExistingComment_ShouldReturnComment() {
        // Arrange
        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));

        // Act
        Optional<Comment> result = commentService.getCommentById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testComment, result.get());
        verify(commentRepository).findById(1);
    }

    @Test
    void getCommentById_NonExistingComment_ShouldReturnEmpty() {
        // Arrange
        when(commentRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Comment> result = commentService.getCommentById(999);

        // Assert
        assertFalse(result.isPresent());
        verify(commentRepository).findById(999);
    }

    @Test
    void getCommentsByFilter_WithThreadId_ShouldReturnThreadComments() {
        // Arrange
        Integer threadId = 1;
        Page<Comment> commentPage = new PageImpl<>(Collections.singletonList(testComment));
        when(commentRepository.findByThread_Id(threadId, pageable)).thenReturn(commentPage);

        // Act
        Page<Comment> result = commentService.getCommentsByFilter(threadId, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testComment, result.getContent().get(0));
        verify(commentRepository).findByThread_Id(threadId, pageable);
    }

    @Test
    void getCommentsByFilter_WithoutThreadId_ShouldReturnAllComments() {
        // Arrange
        Page<Comment> commentPage = new PageImpl<>(Collections.singletonList(testComment));
        when(commentRepository.findAll(pageable)).thenReturn(commentPage);

        // Act
        Page<Comment> result = commentService.getCommentsByFilter(null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testComment, result.getContent().get(0));
        verify(commentRepository).findAll(pageable);
    }

    @Test
    void createComment_Success_ShouldCreateComment() {
        // Arrange
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("New Comment");
        commentRequest.setThreadId(1);

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));
            when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

            // Act
            Comment result = commentService.createComment(commentRequest);

            // Assert
            assertNotNull(result);
            assertEquals(testComment, result);
            verify(commentRepository).save(any(Comment.class));
        }
    }

    @Test
    void createComment_UserNotFound_ShouldThrowException() {
        // Arrange
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("New Comment");
        commentRequest.setThreadId(1);

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> commentService.createComment(commentRequest));
        }
    }

    @Test
    void createComment_ThreadNotFound_ShouldThrowException() {
        // Arrange
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("New Comment");
        commentRequest.setThreadId(999);

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(threadRepository.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ThreadNotFoundException.class, () -> commentService.createComment(commentRequest));
        }
    }

    @Test
    void createComment_ThreadClosed_ShouldThrowException() {
        // Arrange
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("New Comment");
        commentRequest.setThreadId(1);

        testThread.setClosedAt(LocalDateTime.now());

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(threadRepository.findById(1)).thenReturn(Optional.of(testThread));

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> commentService.createComment(commentRequest));
        }
    }

    @Test
    void updateComment_Success_ShouldUpdateComment() {
        // Arrange
        String newContent = "Updated Content";

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));
            when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

            // Act
            Comment result = commentService.updateComment(1, newContent);

            // Assert
            assertNotNull(result);
            verify(commentRepository).save(any(Comment.class));
        }
    }

    @Test
    void updateComment_CommentNotFound_ShouldThrowException() {
        // Arrange
        String newContent = "Updated Content";
        when(commentRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CommentNotFoundException.class, () -> commentService.updateComment(999, newContent));
    }

    @Test
    void updateComment_Unauthorized_ShouldThrowException() {
        // Arrange
        String newContent = "Updated Content";

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn("2"); // Different user
            when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));

            // Act & Assert
            assertThrows(UnauthorizedActionException.class, () -> commentService.updateComment(1, newContent));
        }
    }

    @Test
    void updateComment_ThreadClosed_ShouldThrowException() {
        // Arrange
        String newContent = "Updated Content";
        testThread.setClosedAt(LocalDateTime.now());

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> commentService.updateComment(1, newContent));
        }
    }

    @Test
    void deleteComment_Success_ShouldDeleteComment() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));
            doNothing().when(commentRepository).deleteById(1);

            // Act
            commentService.deleteComment(1);

            // Assert
            verify(commentRepository).deleteById(1);
        }
    }

    @Test
    void deleteComment_CommentNotFound_ShouldThrowException() {
        // Arrange
        when(commentRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReplyNotFoundException.class, () -> commentService.deleteComment(999));
    }

    @Test
    void deleteComment_Unauthorized_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn("2"); // Different user
            when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));

            // Act & Assert
            assertThrows(UnauthorizedActionException.class, () -> commentService.deleteComment(1));
        }
    }
}