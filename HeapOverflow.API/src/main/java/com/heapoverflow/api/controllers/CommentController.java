package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.models.CommentRequest;
import com.heapoverflow.api.services.CommentService;
import com.heapoverflow.api.utils.ApiConstants;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
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
    public ResponseEntity<Page<Comment>> getComments(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Integer threadId,
            @PageableDefault(size = ApiConstants.DEFAULT_PAGE_SIZE) @SortDefault.SortDefaults({
                    @SortDefault(sort = "commentUpvotesCount", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            }) Pageable pageable) {
        Page<Comment> comments = commentService.getCommentsByFilter(userId, threadId, pageable);

        return comments.hasContent() ? ResponseEntity.ok(comments) : ResponseEntity.notFound().build();
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Integer id) {
        return commentService.getCommentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** POST endpoint */

    @PostMapping("/comments")
    public ResponseEntity<Comment> createComment(@RequestBody CommentRequest commentRequest) {
        Comment newComment = commentService.createComment(commentRequest);
        return ResponseEntity.ok(newComment);
    }

    /** PUT endpoint */

    @PutMapping("/comments/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Integer id, @RequestBody String content) {
        Comment updateComment = commentService.updateComment(id, content);
        return ResponseEntity.ok(updateComment);
    }

    /** DELETE endpoint */

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

}
