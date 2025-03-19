package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.models.ThreadRequest;
import com.heapoverflow.api.models.ThreadUpdate;
import com.heapoverflow.api.services.ThreadService;
import com.heapoverflow.api.utils.ApiConstants;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
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
        @RequestParam(required = false) String searchText,
        @PageableDefault(size = ApiConstants.DEFAULT_PAGE_SIZE) 
        @SortDefault.SortDefaults({
            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
        }) Pageable pageable) {

        Page<Thread> threads = threadService.getThreadsByFilter(searchText, pageable);

        return threads.hasContent() ? ResponseEntity.ok(threads) : ResponseEntity.notFound().build();
    }

    @GetMapping("/threads/trending")
    public ResponseEntity<Page<Thread>> getThreadsTrending(
        @PageableDefault(size = ApiConstants.DEFAULT_PAGE_SIZE) Pageable pageable) {

        Page<Thread> threads = threadService.getThreadsByTrending(pageable);

        return threads.hasContent() ? ResponseEntity.ok(threads) : ResponseEntity.notFound().build();
    }

    @GetMapping("/threads/users")
    public ResponseEntity<Page<Thread>> getThreadsUser(
        @PageableDefault(size = ApiConstants.DEFAULT_PAGE_SIZE) 
        @SortDefault.SortDefaults({
            @SortDefault(sort = "threadUpvotesCount", direction = Sort.Direction.DESC),
            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
        }) Pageable pageable){

        Page<Thread> threads = threadService.getThreadsForCurrentUser(pageable);

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

    /** PATCH endpoint */

    @PatchMapping("/threads/{id}")
    public ResponseEntity<Thread> updateThread(@PathVariable Integer id, @RequestBody ThreadUpdate threadUpdate) {
        Thread updatedThread = threadService.updateThread(id, threadUpdate);
        return ResponseEntity.ok(updatedThread);
    }

    /** DELETE endpoint */

    @DeleteMapping("/threads/{id}")
    public ResponseEntity<Void> deleteThread(@PathVariable Integer id) {
        threadService.deleteThread(id);
        return ResponseEntity.noContent().build();
    }

}