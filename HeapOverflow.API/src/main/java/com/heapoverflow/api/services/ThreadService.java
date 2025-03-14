package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.models.ThreadRequest;
import com.heapoverflow.api.repositories.ThreadRepository;
import com.heapoverflow.api.repositories.UserRepository;
import com.heapoverflow.api.exceptions.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ThreadService {

    private final ThreadRepository threadRepository;
    private final UserRepository userRepository;

    public ThreadService(ThreadRepository threadRepository, UserRepository userRepository) {
        this.threadRepository = threadRepository;
        this.userRepository = userRepository;
    }

    public Page<Thread> getAllThreads(Pageable pageable) {
        return threadRepository.findAll(pageable);
    }

    public Optional<Thread> getThreadById(Integer id) {
        return threadRepository.findById(id);
    }

    public Page<Thread> getThreadsByFilter(String title, String description, Pageable pageable) {
        if (title != null && description != null) {
            return threadRepository.findByTitleContainingIgnoreCaseAndDescriptionContainingIgnoreCase(title, description, pageable);
        } else if (title != null) {
            return threadRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else if (description != null) {
            return threadRepository.findByDescriptionContainingIgnoreCase(description, pageable);
        } else {
            return threadRepository.findAll(pageable);
        }
    }

    public Thread createThread(ThreadRequest threadRequest) {
        User user = userRepository.findById(threadRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Thread newThread = new Thread(threadRequest.getTitle(), threadRequest.getDescription(), user);
        return threadRepository.save(newThread);
    }
}
