package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.models.ThreadRequest;
import com.heapoverflow.api.repositories.ThreadRepository;
import com.heapoverflow.api.repositories.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ThreadController {

    private final ThreadRepository threadRepository;
    private final UserRepository userRepository;

    public ThreadController(ThreadRepository threadRepository, UserRepository userRepository) {
        this.threadRepository = threadRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/threads")
    public Page<Thread> getAllThreads(Pageable pageable) {
        return threadRepository.findAll(pageable);
    }

    @GetMapping("/threads/{id}")
    public Optional<Thread> getThreadById(@PathVariable Integer id) {
        return threadRepository.findById(id);
    }

    @GetMapping("/threads/title/{title}")
    public Page<Thread> getThreadsByTitle(@PathVariable String title, Pageable pageable) {
        return threadRepository.findByTitleContaining(title, pageable);
    }

    @PostMapping("/threads")
    public ResponseEntity<?> createThread(@RequestBody ThreadRequest threadRequest) {

        System.out.println(threadRequest);
        Optional<User> user = userRepository.findById(threadRequest.getUserId());

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"User not found\"}");
        }

        Thread newThread = new Thread(threadRequest.getTitle(), threadRequest.getDescription(), user.get());
        return ResponseEntity.ok(threadRepository.save(newThread));
    }
}