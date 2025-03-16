package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.models.CommentRequest;
import com.heapoverflow.api.repositories.CommentRepository;
import com.heapoverflow.api.repositories.ThreadRepository;
import com.heapoverflow.api.repositories.UserRepository;
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

    public Page<Comment> getCommentsByUserId(String id, Pageable pageable) {
        return commentRepository.findByUser_Id(id, pageable);
    }

    public Page<Comment> getCommentsByThreadId(Integer id, Pageable pageable) {
        return commentRepository.findByThread_Id(id, pageable);
    }

    @Transactional
    public Comment createComment(CommentRequest commentRequest) {
        User user = userRepository.findById(commentRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Thread thread = threadRepository.findById(commentRequest.getThreadId())
                .orElseThrow(() -> new ThreadNotFoundException("Thread not found"));

        Comment newComment = new Comment(commentRequest.getContent(), user, thread);
        return commentRepository.save(newComment);
    }

    @Transactional
    public void deleteComment(Integer id) {
        if (!commentRepository.existsById(id)) {
            throw new CommentNotFoundException("Comment with ID " + id + " not found.");
        }
        commentRepository.deleteById(id);
    }
}
