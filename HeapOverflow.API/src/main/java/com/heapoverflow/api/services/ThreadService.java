package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.models.ThreadRequest;
import com.heapoverflow.api.models.ThreadUpdate;
import com.heapoverflow.api.repositories.ThreadRepository;
import com.heapoverflow.api.repositories.UserRepository;
import com.heapoverflow.api.exceptions.*;
import com.heapoverflow.api.utils.AuthUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
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

    public Page<Thread> getThreadsByUserId(String userId, Pageable pageable) {
        return threadRepository.findByUser_Id(userId, pageable);
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

    @Transactional
    public Thread createThread(ThreadRequest threadRequest) {
        String authenticatedUserId = AuthUtils.getAuthenticatedUserId();

        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Thread newThread = new Thread(threadRequest.getTitle(), threadRequest.getDescription(), user);
        return threadRepository.save(newThread);
    }
    
    @Transactional
    public Thread updateThread(Integer threadId, ThreadUpdate threadUpdate) {

        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new ThreadNotFoundException("Thread with ID " + threadId + " not found."));
        
        if (!thread.getUser().getId().equals(AuthUtils.getAuthenticatedUserId())) {
            throw new UnauthorizedActionException("You do not have permission to update this thread.");
        }
    
        if (thread.getClosedAt() != null) {
            throw new IllegalStateException("Thread is already closed.");
        }
    
        // update thread with new info
        if (threadUpdate.getTitle() != null) {
            thread.setTitle(threadUpdate.getTitle());
        }
        if (threadUpdate.getDescription() != null) {
            thread.setDescription(threadUpdate.getDescription());
        }
        if (threadUpdate.getClosedAt() != null) {
            thread.setClosedAt(threadUpdate.getClosedAt());
        }
    
        return threadRepository.save(thread);
    }
    

    @Transactional
    public void deleteThread(Integer id) {
        Thread thread = threadRepository.findById(id)
        .orElseThrow(() -> new ThreadNotFoundException("Thread with ID " + id + " not found."));

        if (!thread.getUser().getId().equals(AuthUtils.getAuthenticatedUserId())) {
            throw new UnauthorizedActionException("You do not have permission to delete this thread.");
        }

        threadRepository.deleteById(thread.getId());
    }
}
