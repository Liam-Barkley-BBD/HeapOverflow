package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.ThreadUpvote;
import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.models.ThreadUpvoteRequest;
import com.heapoverflow.api.repositories.ThreadUpvoteRepository;
import com.heapoverflow.api.repositories.ThreadRepository;
import com.heapoverflow.api.repositories.UserRepository;
import com.heapoverflow.api.exceptions.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    public Page<ThreadUpvote> getAllThreadUpvotes(Pageable pageable) {
        return threadUpvoteRepository.findAll(pageable);
    }

    public Optional<ThreadUpvote> getThreadUpvoteById(Integer id) {
        return threadUpvoteRepository.findById(id);
    }

    public Page<ThreadUpvote> getThreadUpvotesByUserId(String id, Pageable pageable) {
        return threadUpvoteRepository.findByUserId(id, pageable);
    }

    public Page<ThreadUpvote> getThreadUpvotesByThreadId(Integer id, Pageable pageable) {
        return threadUpvoteRepository.findByThreadId(id, pageable);
    }

    @Transactional
    public ThreadUpvote createThreadUpvote(ThreadUpvoteRequest threadUpvoteRequest) {
        User user = userRepository.findById(threadUpvoteRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Thread thread = threadRepository.findById(threadUpvoteRequest.getThreadId())
                .orElseThrow(() -> new ThreadNotFoundException("Thread not found"));

        ThreadUpvote newThreadUpvote = new ThreadUpvote(user, thread);
        return threadUpvoteRepository.save(newThreadUpvote);
    }
}
