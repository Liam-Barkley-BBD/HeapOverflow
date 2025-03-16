package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.CommentUpvote;
import com.heapoverflow.api.models.CommentUpvoteRequest;
import com.heapoverflow.api.services.CommentUpvoteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentUpvoteController {

    private final CommentUpvoteService commentUpvoteService;

    public CommentUpvoteController(CommentUpvoteService commentUpvoteService) {
        this.commentUpvoteService = commentUpvoteService;
    }

    /** GET endpoints */

    @GetMapping("/commentupvotes")
    public ResponseEntity<Page<CommentUpvote>> getCommentUpvotes(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Integer commentId,
            Pageable pageable) {
        Page<CommentUpvote> commentUpvotes = commentUpvoteService.getCommentUpvotesByFilter(userId, commentId,
                pageable);

        return commentUpvotes.hasContent() ? ResponseEntity.ok(commentUpvotes) : ResponseEntity.notFound().build();
    }

    @GetMapping("/commentupvotes/{id}")
    public ResponseEntity<CommentUpvote> getCommentUpvoteById(@PathVariable Integer id) {
        return commentUpvoteService.getCommentUpvoteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** POST endpoint */

    @PostMapping("/commentupvotes")
    public ResponseEntity<CommentUpvote> createCommentUpvote(@RequestBody CommentUpvoteRequest commentUpvoteRequest) {
        CommentUpvote newCommentUpvote = commentUpvoteService.createCommentUpvote(commentUpvoteRequest);
        return ResponseEntity.ok(newCommentUpvote);
    }

    /** DELETE endpoint */

    @DeleteMapping("/commentupvotes/{id}")
    public ResponseEntity<Void> deleteCommentUpvote(@PathVariable Integer id) {
        commentUpvoteService.deleteCommentUpvote(id);
        return ResponseEntity.noContent().build();
    }

}
