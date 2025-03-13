package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.ThreadUpvotes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadUpvotesRepository extends JpaRepository<ThreadUpvotes, Integer> {
    
    Optional<ThreadUpvotes> findById(Integer id);

    Page<ThreadUpvotes> findByThreadId(Integer threadId, Pageable pageable);
}
