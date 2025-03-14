package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.ThreadUpvote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThreadUpvoteRepository extends JpaRepository<ThreadUpvote, Integer> {
    
    Optional<ThreadUpvote> findById(Integer id);

    Page<ThreadUpvote> findByThreadId(Integer threadId, Pageable pageable);
}
