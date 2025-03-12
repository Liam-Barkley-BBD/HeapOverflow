package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Reply;
import com.heapoverflow.api.entities.ThreadUpvote;
import com.heapoverflow.api.repositories.ThreadUpvoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;



@RestController
@RequestMapping("/api")
public class ThreadUpvotesController {

    private final ThreadUpvoteRepository threadUpvoteRepository;

    public ThreadUpvotesController(ThreadUpvoteRepository threadUpvotesRepository) {
        this.threadUpvoteRepository = threadUpvotesRepository;
    }

    /** GET endpoints */
    
    @GetMapping("/threadupvotes")
    public Page<ThreadUpvote> getAllThreadUpvotes(Pageable pageable) {
        System.out.println("Hello, this is a console log in Java!");
        return threadUpvoteRepository.findAll(pageable);
    }

    @GetMapping("/threadupvotes/{id}")
    public Optional<ThreadUpvote> getThreadUpvoteById(@PathVariable Integer id) {
        return threadUpvoteRepository.findById(id);
    }

    @GetMapping("/threadupvotes/thread/{threadId}")
    public Page<ThreadUpvote> getThreadUpvotesByThreadId(@PathVariable Integer threadId, Pageable pageable) {
        return threadUpvoteRepository.findByThreadId(threadId, pageable);
    }

    /** POST endpoints */

    @PostMapping("/threadupvotes")
    public ResponseEntity<?> createThreadUpvote(@RequestBody ThreadUpvote thread) {
        return ResponseEntity.ok(threadUpvoteRepository.save(thread));
    }
}
