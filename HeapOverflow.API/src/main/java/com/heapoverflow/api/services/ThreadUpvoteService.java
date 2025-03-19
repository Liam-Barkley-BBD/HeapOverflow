package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.ThreadUpvote;
import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.repositories.ThreadUpvoteRepository;
import com.heapoverflow.api.repositories.ThreadRepository;
import com.heapoverflow.api.repositories.UserRepository;
import com.heapoverflow.api.utils.AuthUtils;
import com.heapoverflow.api.exceptions.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThreadUpvoteService {

    private final ThreadUpvoteRepository threadUpvoteRepository;
    private final UserRepository userRepository;
    private final ThreadRepository threadRepository;

    public ThreadUpvoteService(ThreadUpvoteRepository threadUpvoteRepository, UserRepository userRepository, ThreadRepository threadRepository) {
        this.threadUpvoteRepository = threadUpvoteRepository;
        this.userRepository = userRepository;
        this.threadRepository = threadRepository;
    }

    @Transactional
    public ThreadUpvote createThreadUpvote(Integer threadId) {
        String authenticatedUserId = AuthUtils.getAuthenticatedUserId();

        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new ThreadNotFoundException("Thread not found"));

        if (threadUpvoteRepository.existsByUserAndThread(user, thread)) {
            throw new IllegalStateException("User has already upvoted the thread");
        }
        else {
            // User has not already upvoted the thread
        }
        
        ThreadUpvote newThreadUpvote = new ThreadUpvote(user, thread);
        return threadUpvoteRepository.save(newThreadUpvote);
    }

    @Transactional
    public void deleteThreadUpvote(Integer threadId) {
        String authenticatedUserId = AuthUtils.getAuthenticatedUserId();

        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Thread thread = threadRepository.findById(threadId)
            .orElseThrow(() -> new ThreadNotFoundException("Thread not found"));
        
        ThreadUpvote threadUpvote = threadUpvoteRepository.findByUserAndThread(user, thread)
            .orElseThrow(() -> new ThreadNotFoundException("ThreadUpvote with thread ID: " + threadId + " for user not found"));

        threadUpvoteRepository.deleteById(threadUpvote.getId());
    }
}
