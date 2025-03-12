package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.CommentUpvote;
import com.heapoverflow.api.entities.ThreadUpvote;
import com.heapoverflow.api.repositories.CommentUpvoteRepository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentUpvotesController {

    private final CommentUpvoteRepository commentUpvoteRepository;

    public CommentUpvotesController(CommentUpvoteRepository commentUpvotesRepository) {
        this.commentUpvoteRepository = commentUpvotesRepository;
    }

    @GetMapping("/commentupvotes")
    public Page<CommentUpvote> getAllCommentUpvotes(Pageable pageable) {
        return commentUpvoteRepository.findAll(pageable);
    }

    @GetMapping("/commentupvotes/{id}")
    public Optional<CommentUpvote> getCommentUpvoteById(@PathVariable Integer id) {
        return commentUpvoteRepository.findById(id);
    }

    @GetMapping("/commentupvotes/comment/{commentId}")
    public Page<CommentUpvote> getCommentUpvotesByCommentId(@PathVariable Integer commentId, Pageable pageable) {
        return commentUpvoteRepository.findByCommentId(commentId, pageable);
    }

    /** POST endpoints */

    @PostMapping("/commentupvotes")
    public ResponseEntity<?> createCommentUpvote(@RequestBody CommentUpvote commentUpvote) {
        return ResponseEntity.ok(commentUpvoteRepository.save(commentUpvote));
    }
}
