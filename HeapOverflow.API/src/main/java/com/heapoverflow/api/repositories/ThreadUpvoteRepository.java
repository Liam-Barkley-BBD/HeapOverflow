package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.ThreadUpvote;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.entities.Thread;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThreadUpvoteRepository extends JpaRepository<ThreadUpvote, Integer> {
    
    Optional<ThreadUpvote> findById(Integer id);

    Page<ThreadUpvote> findByThread_Id(Integer threadId, Pageable pageable);

    Page<ThreadUpvote> findByUser_Id(String userId, Pageable pageable);

    boolean existsByUserAndThread(User user, Thread thread);

    Optional<ThreadUpvote> findByUserAndThread(User user, Thread thread);
}
