package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.models.CommentRequest;
import com.heapoverflow.api.repositories.CommentRepository;
import com.heapoverflow.api.repositories.ThreadRepository;
import com.heapoverflow.api.repositories.UserRepository;
import com.heapoverflow.api.utils.AuthUtils;
import com.heapoverflow.api.exceptions.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ThreadRepository threadRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, ThreadRepository threadRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.threadRepository = threadRepository;
    }

    public Page<Comment> getAllComments(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    public Optional<Comment> getCommentById(Integer id) {
        return commentRepository.findById(id);
    }

    public Page<Comment> getCommentsByFilter(Integer threadId, Pageable pageable) {
        if (threadId != null) {
            return commentRepository.findByThread_Id(threadId, pageable);
        } else {
            return commentRepository.findAll(pageable);
        }
    }

    @Transactional
    public Comment createComment(CommentRequest commentRequest) {

        String authenticatedUserId = AuthUtils.getAuthenticatedUserId();

        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Thread thread = threadRepository.findById(commentRequest.getThreadId())
                .orElseThrow(() -> new ThreadNotFoundException("Thread not found"));

        Comment newComment = new Comment(commentRequest.getContent(), user, thread);
        return commentRepository.save(newComment);
    }

    @Transactional
    public Comment updateComment(Integer commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment with ID " + commentId + " not found."));
        
        if (!comment.getUser().getId().equals(AuthUtils.getAuthenticatedUserId())) {
            throw new UnauthorizedActionException("You do not have permission to update this comment.");
        }
    
        if (comment.getThread().getClosedAt() != null) {
            throw new IllegalStateException("Thread is already closed.");
        }
    
        // update comment with new info
        if (content != null) {
            comment.setContent(content);
        }

        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Integer id) {
        Comment comment = commentRepository.findById(id)
        .orElseThrow(() -> new ReplyNotFoundException("Comment with ID " + id + " not found."));

        if (!comment.getUser().getId().equals(AuthUtils.getAuthenticatedUserId())) {
            throw new UnauthorizedActionException("You do not have permission to delete this comment.");
        }

        commentRepository.deleteById(comment.getId());
    }
}
