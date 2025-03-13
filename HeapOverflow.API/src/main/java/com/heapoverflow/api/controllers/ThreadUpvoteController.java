package com.heapoverflow.api.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.heapoverflow.api.entities.ThreadUpvote;
import com.heapoverflow.api.repositories.ThreadRepository;
import com.heapoverflow.api.repositories.UserRepository;
import com.heapoverflow.api.repositories.ThreadUpvoteRepository;

import java.util.Optional;

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

    @GetMapping("/thread/upvotes")
    public Page<ThreadUpvote> getAllThreadUpvotes(Pageable pageable) {
        return threadUpvoteRepository.findAll(pageable);
    }

    @GetMapping("/thread/upvotes/{threadId}")
    public Page<ThreadUpvote> getThreadById(@PathVariable Integer threadId, Pageable pageable) {
        return threadUpvoteRepository.findByThread_Id(threadId, pageable);
    }

}
