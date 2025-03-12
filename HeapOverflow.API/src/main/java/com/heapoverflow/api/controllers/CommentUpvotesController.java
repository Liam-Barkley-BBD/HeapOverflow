package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.CommentUpvotes;
import com.heapoverflow.api.repositories.CommentUpvotesRepository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentUpvotesController {

    private final CommentUpvotesRepository commentUpvotesRepository;

    public CommentUpvotesController(CommentUpvotesRepository commentUpvotesRepository) {
        this.commentUpvotesRepository = commentUpvotesRepository;
    }

    @GetMapping("/commentupvotes")
    public Page<CommentUpvotes> getAllCommentUpvotes(Pageable pageable) {
        return commentUpvotesRepository.findAll(pageable);
    }

    @GetMapping("/commentupvotes/{id}")
    public Optional<CommentUpvotes> getCommentUpvoteById(@PathVariable Integer id) {
        return commentUpvotesRepository.findById(id);
    }

    @GetMapping("/commentupvotes/comment/{commentId}")
    public Page<CommentUpvotes> getCommentUpvotesByCommentId(@PathVariable Integer commentId, Pageable pageable) {
        return commentUpvotesRepository.findByCommentId(commentId, pageable);
    }
}
