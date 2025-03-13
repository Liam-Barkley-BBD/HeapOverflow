package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.repositories.CommentRepository;
import com.heapoverflow.api.repositories.ThreadRepository;
import com.heapoverflow.api.repositories.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentRepository commentRepository;
    private final ThreadRepository threadRepository;
    private final UserRepository userRepository;

    public CommentController(CommentRepository commentRepository, ThreadRepository threadRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.threadRepository = threadRepository;
        this.userRepository = userRepository;
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

    /** POST endpoints */

    @PostMapping("/comments")
    public ResponseEntity<?> createComment(@RequestBody Comment comment) {
        Thread thread = threadRepository.findById(comment.getThread().getId())
                .orElse(null);
        User user = userRepository.findById(comment.getUser().getId())
                .orElse(null);
 
        if (thread == null) {
            return ResponseEntity.badRequest().body("{\"error\": \"Thread does not exist\"}");
        }
        else if (user == null) {
            return ResponseEntity.badRequest().body("{\"error\": \"User does not exist\"}");
        }
        else {
            comment.setThread(thread);
            comment.setUser(user);
            comment.setCreatedAt(LocalDateTime.now());
            
            return ResponseEntity.ok(commentRepository.save(comment));
        }
    }
}
