package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Reply;
import com.heapoverflow.api.models.ReplyRequest;
import com.heapoverflow.api.services.ReplyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ReplyController {

    private final ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }

    /** GET endpoints */

    @GetMapping("/replies")
    public ResponseEntity<Page<Reply>> getReplies(Pageable pageable) {

        Page<Reply> replies = replyService.getAllReplies(pageable);

        return replies.hasContent() ? ResponseEntity.ok(replies) : ResponseEntity.notFound().build();
    }

    @GetMapping("/replies/{id}")
    public ResponseEntity<Reply> getReplyById(@PathVariable Integer id) {
        return replyService.getReplyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/replies/user/{userId}")
    public ResponseEntity<Page<Reply>> getRepliesByUserId(@PathVariable String userId, Pageable pageable) {
        Page<Reply> replies =  replyService.getRepliesByUserId(userId, pageable);
        
        return replies.hasContent() ? ResponseEntity.ok(replies) : ResponseEntity.notFound().build();
    }

    @GetMapping("/replies/comment/{commentId}")
    public ResponseEntity<Page<Reply>> getRepliesByCommentId(@PathVariable Integer commentId, Pageable pageable) {
        Page<Reply> replies =  replyService.getRepliesByCommentId(commentId, pageable);
        
        return replies.hasContent() ? ResponseEntity.ok(replies) : ResponseEntity.notFound().build();
    }

    /** POST endpoint */

    @PostMapping("/replies")
    public ResponseEntity<Reply> createReply(@RequestBody ReplyRequest replyRequest) {
        Reply newReply = replyService.createReply(replyRequest);
        return ResponseEntity.ok(newReply);
    }

    /** DELETE endpoint */

    @DeleteMapping("/replies/{id}")
    public ResponseEntity<Void> deleteReply(@PathVariable Integer id) {
        replyService.deleteReply(id);
        return ResponseEntity.noContent().build();
    }

}
