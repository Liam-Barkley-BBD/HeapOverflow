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

    /** GET endpoints */

    @GetMapping("/threadupvotes")
    public ResponseEntity<Page<ThreadUpvote>> getThreadUpvotes(Pageable pageable) {
        Page<ThreadUpvote> threadUpvotes = threadUpvoteService.getAllThreadUpvotes(pageable);

        return threadUpvotes.hasContent() ? ResponseEntity.ok(threadUpvotes) : ResponseEntity.notFound().build();
    }

    @GetMapping("/threadupvotes/{id}")
    public ResponseEntity<ThreadUpvote> getThreadUpvoteById(@PathVariable Integer id) {
        return threadUpvoteService.getThreadUpvoteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/threadupvotes/user/{userId}")
    public ResponseEntity<Page<ThreadUpvote>> getThreadUpvotesByUserId(@PathVariable String userId, Pageable pageable) {
        Page<ThreadUpvote> threadUpvotes =  threadUpvoteService.getThreadUpvotesByUserId(userId, pageable);
        
        return threadUpvotes.hasContent() ? ResponseEntity.ok(threadUpvotes) : ResponseEntity.notFound().build();
    }

    @GetMapping("/threadupvotes/thread/{threadId}")
    public ResponseEntity<Page<ThreadUpvote>> getThreadUpvotesByThreadId(@PathVariable Integer threadId, Pageable pageable) {
        Page<ThreadUpvote> threadUpvotes =  threadUpvoteService.getThreadUpvotesByThreadId(threadId, pageable);
        
        return threadUpvotes.hasContent() ? ResponseEntity.ok(threadUpvotes) : ResponseEntity.notFound().build();
    }

    /** POST endpoint */

    @PostMapping("/threadupvotes")
    public ResponseEntity<ThreadUpvote> createThreadUpvote(@RequestBody ThreadUpvoteRequest threadUpvoteRequest) {
        ThreadUpvote newThreadUpvote = threadUpvoteService.createThreadUpvote(threadUpvoteRequest);
        return ResponseEntity.ok(newThreadUpvote);
    }

}
