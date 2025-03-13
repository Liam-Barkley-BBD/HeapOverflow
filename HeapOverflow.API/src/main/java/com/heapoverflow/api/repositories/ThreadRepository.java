package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.Thread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ThreadRepository extends PagingAndSortingRepository<Thread, Integer> {

    Optional<Thread> findById(Integer id);

    List<Thread> findByUserId(String id, Pageable pageable);

    Page<Thread> findByTitleContaining(String title, Pageable pageable);

    Page<Thread> findByTitleContainingOrDescriptionContaining(String searchText, String searchText2, Pageable pageable);

}
