package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.Thread;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThreadRepository extends JpaRepository<Thread, Integer> {

    Page<Thread> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Thread> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    Page<Thread> findByTitleContainingIgnoreCaseAndDescriptionContainingIgnoreCase(String searchText1, String searchText2, Pageable pageable);
}
