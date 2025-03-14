package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.models.CommentRequest;
import com.heapoverflow.api.repositories.CommentRepository;
import com.heapoverflow.api.repositories.ThreadRepository;
import com.heapoverflow.api.repositories.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ThreadRepository threadRepository;

    public CommentController(CommentRepository commentRepository, UserRepository userRepository,
            ThreadRepository threadRepository) {

        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.threadRepository = threadRepository;
        
    }

    /** GET endpoints */

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
    public ResponseEntity<?> createComment(@RequestBody CommentRequest commentRequest) {

        Optional<User> user = userRepository.findById(commentRequest.getUserId());

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"User not found\"}");
        }

        Optional<Thread> thread = threadRepository.findById(commentRequest.getThreadId());

        if (thread.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Thread ID not found\"}");
        }

        Comment newComment = new Comment(commentRequest.getContent(), user.get(), thread.get());
        return ResponseEntity.ok(commentRepository.save(newComment));
    }
}
