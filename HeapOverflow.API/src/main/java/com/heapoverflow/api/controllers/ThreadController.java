package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.repositories.ThreadRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ThreadController {

    private final ThreadRepository threadRepository;

    public ThreadController(ThreadRepository threadRepository) {
        this.threadRepository = threadRepository;
    }

    @GetMapping("/threads")
    public List<Thread> getAllThreads() {
        return threadRepository.findAll();
    }

    @GetMapping("/threads/{id}")
    public Optional<Thread> getThreadById(@PathVariable Integer id) {
        return threadRepository.findById(id);
    }

    @GetMapping("/threads/title/{title}")
    public List<Thread> getThreadsByTitle(@PathVariable String title) {
        return threadRepository.findByTitleContaining(title);
    }
}
