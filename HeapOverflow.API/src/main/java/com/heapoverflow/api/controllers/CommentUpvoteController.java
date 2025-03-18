package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.CommentUpvote;
import com.heapoverflow.api.models.CommentUpvoteRequest;
import com.heapoverflow.api.services.CommentUpvoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentUpvoteController {

    private final CommentUpvoteService commentUpvoteService;

    public CommentUpvoteController(CommentUpvoteService commentUpvoteService) {
        this.commentUpvoteService = commentUpvoteService;
    }

    /** POST endpoint */

    @PostMapping("/commentupvotes")
    public ResponseEntity<CommentUpvote> createCommentUpvote(@RequestBody CommentUpvoteRequest commentUpvoteRequest) {
        CommentUpvote newCommentUpvote = commentUpvoteService.createCommentUpvote(commentUpvoteRequest.getCommentId());
        return ResponseEntity.ok(newCommentUpvote);
    }

    /** DELETE endpoint */

    @DeleteMapping("/commentupvotes")
    public ResponseEntity<Void> deleteCommentUpvote(@RequestParam(required = true) Integer commentId) {
        commentUpvoteService.deleteCommentUpvote(commentId);
        return ResponseEntity.noContent().build();
    }

}
