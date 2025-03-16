package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Reply;
import com.heapoverflow.api.models.ReplyRequest;
import com.heapoverflow.api.services.ReplyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
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
    public ResponseEntity<Page<Reply>> getReplies(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Integer commentId,
            @PageableDefault(size = 5) @SortDefault.SortDefaults({
                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            }) Pageable pageable) {

        Page<Reply> replies = replyService.getRepliesByFilter(userId, commentId, pageable);

        return replies.hasContent() ? ResponseEntity.ok(replies) : ResponseEntity.notFound().build();
    }

    @GetMapping("/replies/{id}")
    public ResponseEntity<Reply> getReplyById(@PathVariable Integer id) {
        return replyService.getReplyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** POST endpoint */

    @PostMapping("/replies")
    public ResponseEntity<Reply> createReply(@RequestBody ReplyRequest replyRequest) {
        Reply newReply = replyService.createReply(replyRequest);
        return ResponseEntity.ok(newReply);
    }

    /** PUT endpoint */

    @PutMapping("/replies/{id}")
    public ResponseEntity<Reply> updateReply(@PathVariable Integer id, @RequestBody String content) {
        Reply updateReply = replyService.updateReply(id, content);
        return ResponseEntity.ok(updateReply);
    }
    
    /** DELETE endpoint */

    @DeleteMapping("/replies/{id}")
    public ResponseEntity<Void> deleteReply(@PathVariable Integer id) {
        replyService.deleteReply(id);
        return ResponseEntity.noContent().build();
    }

}
