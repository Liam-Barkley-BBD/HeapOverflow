package com.heapoverflow.api.controllers;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.entities.CommentUpvote;
import com.heapoverflow.api.entities.ThreadUpvote;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.models.CommentUpvoteRequest;
import com.heapoverflow.api.models.ThreadUpvoteRequest;
import com.heapoverflow.api.repositories.CommentRepository;
import com.heapoverflow.api.repositories.UserRepository;
import com.heapoverflow.api.repositories.CommentUpvoteRepository;

@RestController
@RequestMapping("/api")
public class CommentUpvoteController {

    private final CommentUpvoteRepository commentUpvoteRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentUpvoteController(CommentUpvoteRepository commentUpvoteRepository, CommentRepository commentRepository,
            UserRepository userRepository) {
        this.commentUpvoteRepository = commentUpvoteRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    /** GET endpoints */

    @GetMapping("/commentupvotes")
    public Page<CommentUpvote> getAllCommentUpvotes(Pageable pageable) {
        return commentUpvoteRepository.findAll(pageable);
    }

    @GetMapping("/commentupvotes/{commentId}")
    public Page<CommentUpvote> getCommentById(@PathVariable Integer commentId, Pageable pageable) {
        return commentUpvoteRepository.findByCommentId(commentId, pageable);
    }

    /** POST endpoints */

    @PostMapping("/commentupvotes")
    public ResponseEntity<?> createCommentUpvote(@RequestBody CommentUpvoteRequest commentUpvoteRequest) {

        System.out.println(commentUpvoteRequest);
        
        Optional<User> user = userRepository.findById(commentUpvoteRequest.getUserId());

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"User not found\"}");
        }

        Optional<Comment> comment = commentRepository.findById(commentUpvoteRequest.getCommentId());
        
        if (comment.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Comment not found\"}");
        }

        CommentUpvote newCommentUpvote = new CommentUpvote(user.get(), comment.get());
        return ResponseEntity.ok(commentUpvoteRepository.save(newCommentUpvote));
    }
}