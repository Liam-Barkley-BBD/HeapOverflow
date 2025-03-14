package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.models.ThreadRequest;
import com.heapoverflow.api.services.ThreadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ThreadController {

    private final ThreadService threadService;

    public ThreadController(ThreadService threadService) {
        this.threadService = threadService;
    }

    /** GET endpoints */

    @GetMapping("/threads")
    public ResponseEntity<Page<Thread>> getThreads(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            Pageable pageable) {

        Page<Thread> threads = threadService.getThreadsByFilter(title, description, pageable);

        return threads.hasContent() ? ResponseEntity.ok(threads) : ResponseEntity.notFound().build();
    }

    @GetMapping("/threads/{id}")
    public ResponseEntity<Thread> getThreadById(@PathVariable Integer id) {
        return threadService.getThreadById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** POST endpoint */

    @PostMapping("/threads")
    public ResponseEntity<Thread> createThread(@RequestBody ThreadRequest threadRequest) {
        Thread newThread = threadService.createThread(threadRequest);
        return ResponseEntity.ok(newThread);
    }

}
