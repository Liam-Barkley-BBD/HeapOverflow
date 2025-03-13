package com.heapoverflow.api.controllers;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.heapoverflow.api.entities.ThreadUpvote;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.models.ThreadUpvoteRequest;
import com.heapoverflow.api.repositories.ThreadRepository;
import com.heapoverflow.api.repositories.UserRepository;
import com.heapoverflow.api.repositories.ThreadUpvoteRepository;

@RestController
@RequestMapping("/api")
public class ThreadUpvoteController {

    private final ThreadUpvoteRepository threadUpvoteRepository;
    private final ThreadRepository threadRepository;
    private final UserRepository userRepository;

    public ThreadUpvoteController(ThreadUpvoteRepository threadUpvoteRepository, ThreadRepository threadRepository,
            UserRepository userRepository) {
        this.threadUpvoteRepository = threadUpvoteRepository;
        this.threadRepository = threadRepository;
        this.userRepository = userRepository;
    }

    /** GET endpoints */

    @GetMapping("/threadupvotes")
    public Page<ThreadUpvote> getAllThreadUpvotes(Pageable pageable) {
        return threadUpvoteRepository.findAll(pageable);
    }

    @GetMapping("/threadupvotes/{threadId}")
    public Page<ThreadUpvote> getThreadById(@PathVariable Integer threadId, Pageable pageable) {
        return threadUpvoteRepository.findByThreadId(threadId, pageable);
    }

    /** POST endpoints */

    @PostMapping("/threadupvotes")
    public ResponseEntity<?> createThreadUpvote(@RequestBody ThreadUpvoteRequest threadUpvoteRequest) {

        System.out.println(threadUpvoteRequest);
        
        Optional<User> user = userRepository.findById(threadUpvoteRequest.getUserId());

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"User not found\"}");
        }

        Optional<Thread> thread = threadRepository.findById(threadUpvoteRequest.getThreadId());

        if (thread.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Thread not found\"}");
        }

        ThreadUpvote newThreadUpvote = new ThreadUpvote(user.get(), thread.get());
        return ResponseEntity.ok(threadUpvoteRepository.save(newThreadUpvote));
    }
}
