package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.models.CommentRequest;
import com.heapoverflow.api.services.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /** GET endpoints */

    @GetMapping("/comments")
    public ResponseEntity<Page<Comment>> getComments(Pageable pageable) {
        Page<Comment> comments = commentService.getAllComments(pageable);

        return comments.hasContent() ? ResponseEntity.ok(comments) : ResponseEntity.notFound().build();
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Integer id) {
        return commentService.getCommentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/comments/user/{userId}")
    public ResponseEntity<Page<Comment>> getCommentsByUserId(@PathVariable String userId, Pageable pageable) {
        Page<Comment> comments =  commentService.getCommentsByUserId(userId, pageable);
        
        return comments.hasContent() ? ResponseEntity.ok(comments) : ResponseEntity.notFound().build();
    }

    @GetMapping("/comments/thread/{threadId}")
    public ResponseEntity<Page<Comment>> getCommentsByThreadId(@PathVariable Integer threadId, Pageable pageable) {
        Page<Comment> comments =  commentService.getCommentsByThreadId(threadId, pageable);
        
        return comments.hasContent() ? ResponseEntity.ok(comments) : ResponseEntity.notFound().build();
    }

    /** POST endpoint */

    @PostMapping("/comments")
    public ResponseEntity<Comment> createComment(@RequestBody CommentRequest commentRequest) {
        Comment newComment = commentService.createComment(commentRequest);
        return ResponseEntity.ok(newComment);
    }

    /** DELETE endpoint */

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

}
