package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.repositories.ThreadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /** GET endpoints */
    
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

    @GetMapping("/threads/user/{userGoogleId}")
    public List<Thread> getThreadsByUserGoogleId(@PathVariable String userGoogleId, Pageable pageable) {
        return threadRepository.findByUserId(userGoogleId, pageable);
    }

    @GetMapping("/threads/search/{searchText}")
    public Page<Thread> searchThreads(@PathVariable String searchText, Pageable pageable) {
    return threadRepository.findByTitleContainingOrDescriptionContaining(searchText, searchText, pageable);
}

}
