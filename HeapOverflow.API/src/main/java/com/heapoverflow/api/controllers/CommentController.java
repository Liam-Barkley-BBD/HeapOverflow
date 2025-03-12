package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.repositories.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentRepository commentRepository;

    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @GetMapping("/comments")
    public Page<Comment> getAllComments(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    @GetMapping("/comments/{id}")
    public Optional<Comment> getCommentById(@PathVariable Integer id) {
        return commentRepository.findById(id);
    }

    @GetMapping("/comments/thread/{threadId}")
    public Page<Comment> getCommentsByThreadId(@PathVariable Integer threadId, Pageable pageable) {
        return commentRepository.findByThreadId(threadId, pageable);
    }
    
    @GetMapping("/comments/user/{userGoogleId}")
    public Page<Comment> getCommentsByUserGoogleId(@PathVariable String userGoogleId, Pageable pageable) {
        return commentRepository.findByUserId(userGoogleId, pageable);
    }
}
