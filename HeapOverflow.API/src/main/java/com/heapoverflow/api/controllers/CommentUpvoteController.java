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
    public ResponseEntity<Page<CommentUpvote>> getCommentUpvotes(Pageable pageable) {
        Page<CommentUpvote> commentUpvotes = commentUpvoteService.getAllCommentUpvotes(pageable);

        return commentUpvotes.hasContent() ? ResponseEntity.ok(commentUpvotes) : ResponseEntity.notFound().build();
    }

    @GetMapping("/commentUpvotes/{id}")
    public ResponseEntity<CommentUpvote> getCommentUpvoteById(@PathVariable Integer id) {
        return commentUpvoteService.getCommentUpvoteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/commentUpvotes/user/{userId}")
    public ResponseEntity<Page<CommentUpvote>> getCommentUpvotesByUserId(@PathVariable String userId, Pageable pageable) {
        Page<CommentUpvote> commentUpvotes =  commentUpvoteService.getCommentUpvotesByUserId(userId, pageable);
        
        return commentUpvotes.hasContent() ? ResponseEntity.ok(commentUpvotes) : ResponseEntity.notFound().build();
    }

    @GetMapping("/commentUpvotes/comment/{commentId}")
    public ResponseEntity<Page<CommentUpvote>> getCommentUpvotesByCommentId(@PathVariable Integer commentId, Pageable pageable) {
        Page<CommentUpvote> commentUpvotes =  commentUpvoteService.getCommentUpvotesByCommentId(commentId, pageable);
        
        return commentUpvotes.hasContent() ? ResponseEntity.ok(commentUpvotes) : ResponseEntity.notFound().build();
    }

    /** POST endpoint */

    @PostMapping("/commentUpvotes")
    public ResponseEntity<CommentUpvote> createCommentUpvote(@RequestBody CommentUpvoteRequest commentUpvoteRequest) {
        CommentUpvote newCommentUpvote = commentUpvoteService.createCommentUpvote(commentUpvoteRequest);
        return ResponseEntity.ok(newCommentUpvote);
    }

}
