package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.ThreadUpvote;
import com.heapoverflow.api.entities.Thread;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.repositories.ThreadUpvoteRepository;
import com.heapoverflow.api.repositories.ThreadRepository;
import com.heapoverflow.api.repositories.UserRepository;
import com.heapoverflow.api.utils.AuthUtils;
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

    public Page<ThreadUpvote> getThreadUpvotesByFilter(String userId, Integer threadId, Pageable pageable) {
        if (userId != null) {
            return threadUpvoteRepository.findByUser_Id(userId, pageable);
        } else if (threadId != null) {
            return threadUpvoteRepository.findByThread_Id(threadId, pageable);
        } else {
            return threadUpvoteRepository.findAll(pageable);
        }
    }

    @Transactional
    public ThreadUpvote createThreadUpvote(Integer threadId) {
        String authenticatedUserId = AuthUtils.getAuthenticatedUserId();

        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new ThreadNotFoundException("Thread not found"));

        ThreadUpvote newThreadUpvote = new ThreadUpvote(user, thread);
        return threadUpvoteRepository.save(newThreadUpvote);
    }

    @Transactional
    public void deleteThreadUpvote(Integer id) {
        ThreadUpvote threadUpvote = threadUpvoteRepository.findById(id)
        .orElseThrow(() -> new ThreadNotFoundException("ThreadUpvote with ID " + id + " not found."));

        if (!threadUpvote.getUser().getId().equals(AuthUtils.getAuthenticatedUserId())) {
            throw new UnauthorizedActionException("You do not have permission to delete this thread upvote.");
        }

        threadUpvoteRepository.deleteById(threadUpvote.getId());
    }
}
