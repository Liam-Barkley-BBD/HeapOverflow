package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.entities.Reply;
import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.exceptions.CommentNotFoundException;
import com.heapoverflow.api.exceptions.ReplyNotFoundException;
import com.heapoverflow.api.exceptions.UnauthorizedActionException;
import com.heapoverflow.api.exceptions.UserNotFoundException;
import com.heapoverflow.api.models.ReplyRequest;
import com.heapoverflow.api.repositories.CommentRepository;
import com.heapoverflow.api.repositories.ReplyRepository;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReplyServiceTest {

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ReplyService replyService;

    private User testUser;
    private Thread testThread;
    private Comment testComment;
    private Reply testReply;
    private Pageable pageable;
    private final String authenticatedUserId = "1";

    @BeforeEach
    void setUp() {
        testUser = new User(authenticatedUserId, "John Doe", "john@example.com");
        testThread = new Thread("Test Thread", "Test Description", testUser);
        testThread.setId(1);
        testComment = new Comment("Test Comment", testUser, testThread);
        testComment.setId(1);
        testReply = new Reply("Test Reply", testUser, testComment);
        testReply.setId(1);
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getAllReplies_ShouldReturnAllReplies() {
        // Arrange
        Page<Reply> replyPage = new PageImpl<>(Collections.singletonList(testReply));
        when(replyRepository.findAll(pageable)).thenReturn(replyPage);

        // Act
        Page<Reply> result = replyService.getAllReplies(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testReply, result.getContent().get(0));
        verify(replyRepository).findAll(pageable);
    }

    @Test
    void getReplyById_ExistingReply_ShouldReturnReply() {
        // Arrange
        when(replyRepository.findById(1)).thenReturn(Optional.of(testReply));

        // Act
        Optional<Reply> result = replyService.getReplyById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testReply, result.get());
        verify(replyRepository).findById(1);
    }

    @Test
    void getReplyById_NonExistingReply_ShouldReturnEmpty() {
        // Arrange
        when(replyRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Reply> result = replyService.getReplyById(999);

        // Assert
        assertFalse(result.isPresent());
        verify(replyRepository).findById(999);
    }

    @Test
    void getRepliesByFilter_WithCommentId_ShouldReturnCommentReplies() {
        // Arrange
        Page<Reply> replyPage = new PageImpl<>(Collections.singletonList(testReply));
        when(replyRepository.findByComment_Id(1, pageable)).thenReturn(replyPage);

        // Act
        Page<Reply> result = replyService.getRepliesByFilter(1, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testReply, result.getContent().get(0));
        verify(replyRepository).findByComment_Id(1, pageable);
    }

    @Test
    void getRepliesByFilter_NoCommentId_ShouldReturnAllReplies() {
        // Arrange
        Page<Reply> replyPage = new PageImpl<>(Collections.singletonList(testReply));
        when(replyRepository.findAll(pageable)).thenReturn(replyPage);

        // Act
        Page<Reply> result = replyService.getRepliesByFilter(null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(testReply, result.getContent().get(0));
        verify(replyRepository).findAll(pageable);
    }

    @Test
    void createReply_Success_ShouldCreateReply() {
        // Arrange
        ReplyRequest replyRequest = new ReplyRequest();
        replyRequest.setContent("New Reply");
        replyRequest.setCommentId(1);

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));
            when(replyRepository.save(any(Reply.class))).thenReturn(testReply);

            // Act
            Reply result = replyService.createReply(replyRequest);

            // Assert
            assertNotNull(result);
            assertEquals(testReply, result);
            verify(replyRepository).save(any(Reply.class));
        }
    }

    @Test
    void createReply_UserNotFound_ShouldThrowException() {
        // Arrange
        ReplyRequest replyRequest = new ReplyRequest();
        replyRequest.setContent("New Reply");
        replyRequest.setCommentId(1);

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> replyService.createReply(replyRequest));
            verify(replyRepository, never()).save(any(Reply.class));
        }
    }

    @Test
    void createReply_CommentNotFound_ShouldThrowException() {
        // Arrange
        ReplyRequest replyRequest = new ReplyRequest();
        replyRequest.setContent("New Reply");
        replyRequest.setCommentId(999);

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(commentRepository.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(CommentNotFoundException.class, () -> replyService.createReply(replyRequest));
            verify(replyRepository, never()).save(any(Reply.class));
        }
    }

    @Test
    void createReply_ThreadClosed_ShouldThrowException() {
        // Arrange
        ReplyRequest replyRequest = new ReplyRequest();
        replyRequest.setContent("New Reply");
        replyRequest.setCommentId(1);

        testThread.setClosedAt(LocalDateTime.now());

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(testUser));
            when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> replyService.createReply(replyRequest));
            verify(replyRepository, never()).save(any(Reply.class));
        }
    }

    @Test
    void updateReply_Success_ShouldUpdateReply() {
        // Arrange
        String updatedContent = "Updated Reply";

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(replyRepository.findById(1)).thenReturn(Optional.of(testReply));
            when(replyRepository.save(any(Reply.class))).thenReturn(testReply);

            // Act
            Reply result = replyService.updateReply(1, updatedContent);

            // Assert
            assertNotNull(result);
            verify(replyRepository).save(any(Reply.class));
        }
    }

    @Test
    void updateReply_ReplyNotFound_ShouldThrowException() {
        // Arrange
        String updatedContent = "Updated Reply";
        when(replyRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReplyNotFoundException.class, () -> replyService.updateReply(999, updatedContent));
    }

    @Test
    void updateReply_Unauthorized_ShouldThrowException() {
        // Arrange
        String updatedContent = "Updated Reply";
        User differentUser = new User("2", "Jane Doe", "jane@example.com");
        Reply differentUserReply = new Reply("Different Reply", differentUser, testComment);
        differentUserReply.setId(1);

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn("2");
            when(replyRepository.findById(1)).thenReturn(Optional.of(testReply));

            // Act & Assert
            assertThrows(UnauthorizedActionException.class, () -> replyService.updateReply(1, updatedContent));
            verify(replyRepository, never()).save(any(Reply.class));
        }
    }

    @Test
    void updateReply_ThreadClosed_ShouldThrowException() {
        // Arrange
        String updatedContent = "Updated Reply";
        testThread.setClosedAt(LocalDateTime.now());

        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(replyRepository.findById(1)).thenReturn(Optional.of(testReply));

            // Act & Assert
            assertThrows(IllegalStateException.class, () -> replyService.updateReply(1, updatedContent));
            verify(replyRepository, never()).save(any(Reply.class));
        }
    }

    @Test
    void deleteReply_Success_ShouldDeleteReply() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn(authenticatedUserId);
            when(replyRepository.findById(1)).thenReturn(Optional.of(testReply));
            doNothing().when(replyRepository).deleteById(1);

            // Act
            replyService.deleteReply(1);

            // Assert
            verify(replyRepository).deleteById(1);
        }
    }

    @Test
    void deleteReply_ReplyNotFound_ShouldThrowException() {
        // Arrange
        when(replyRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReplyNotFoundException.class, () -> replyService.deleteReply(999));
        verify(replyRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteReply_Unauthorized_ShouldThrowException() {
        // Arrange
        try (MockedStatic<AuthUtils> authUtils = Mockito.mockStatic(AuthUtils.class)) {
            authUtils.when(AuthUtils::getAuthenticatedUserId).thenReturn("2");
            when(replyRepository.findById(1)).thenReturn(Optional.of(testReply));

            // Act & Assert
            assertThrows(UnauthorizedActionException.class, () -> replyService.deleteReply(1));
            verify(replyRepository, never()).deleteById(anyInt());
        }
    }
}