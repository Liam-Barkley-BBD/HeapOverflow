package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.ThreadUpvote;
import com.heapoverflow.api.models.ThreadUpvoteRequest;
import com.heapoverflow.api.services.ThreadUpvoteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ThreadUpvoteController {

    private final ThreadUpvoteService threadUpvoteService;

    public ThreadUpvoteController(ThreadUpvoteService threadUpvoteService) {
        this.threadUpvoteService = threadUpvoteService;
    }

    /** POST endpoint */

    @PostMapping("/threadupvotes")
    public ResponseEntity<ThreadUpvote> createThreadUpvote(@RequestBody ThreadUpvoteRequest threadUpvoteRequest) {
        ThreadUpvote newThreadUpvote = threadUpvoteService.createThreadUpvote(threadUpvoteRequest.getThreadId());
        return ResponseEntity.ok(newThreadUpvote);
    }

    /** DELETE endpoint */

    @DeleteMapping("/threadupvotes")
    public ResponseEntity<Void> deleteThreadUpvote(@RequestParam(required = true) Integer threadId) {
        threadUpvoteService.deleteThreadUpvote(threadId);
        return ResponseEntity.noContent().build();
    }

}
