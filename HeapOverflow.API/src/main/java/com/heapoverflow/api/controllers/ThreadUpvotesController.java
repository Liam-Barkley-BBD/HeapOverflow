package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.ThreadUpvotes;
import com.heapoverflow.api.repositories.ThreadUpvotesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;



@RestController
@RequestMapping("/api")
public class ThreadUpvotesController {

    private final ThreadUpvotesRepository threadUpvotesRepository;

    public ThreadUpvotesController(ThreadUpvotesRepository threadUpvotesRepository) {
        this.threadUpvotesRepository = threadUpvotesRepository;
    }

    /** GET endpoints */
    
    @GetMapping("/threadupvotes")
    public Page<ThreadUpvotes> getAllThreadUpvotes(Pageable pageable) {
        System.out.println("Hello, this is a console log in Java!");
        return threadUpvotesRepository.findAll(pageable);
    }

    @GetMapping("/threadupvotes/{id}")
    public Optional<ThreadUpvotes> getThreadUpvoteById(@PathVariable Integer id) {
        return threadUpvotesRepository.findById(id);
    }

    @GetMapping("/threadupvotes/thread/{threadId}")
    public Page<ThreadUpvotes> getThreadUpvotesByThreadId(@PathVariable Integer threadId, Pageable pageable) {
        return threadUpvotesRepository.findByThreadId(threadId, pageable);
    }
}
