package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.Thread;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ThreadRepository extends JpaRepository<Thread, Integer> {

    Optional<Thread> findById(Integer id);

    Page<Thread> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Thread> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String searchText1, String searchText2, Pageable pageable);
}
