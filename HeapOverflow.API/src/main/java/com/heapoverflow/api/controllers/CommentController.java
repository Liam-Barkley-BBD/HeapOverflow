package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.repositories.CommentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentRepository commentRepository;

    public CommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @GetMapping("/comments")
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @GetMapping("/comments/{threadId}")
    public List<Comment> getCommentsByThreadId(@PathVariable Integer threadId) {
        return commentRepository.findByThreadId(threadId);
    }

    @GetMapping("/comments/user/{userGoogleId}")
    public List<Comment> getCommentsByUserGoogleId(@PathVariable String userGoogleId) {
        return commentRepository.findByUserGoogleId(userGoogleId);
    }
}
