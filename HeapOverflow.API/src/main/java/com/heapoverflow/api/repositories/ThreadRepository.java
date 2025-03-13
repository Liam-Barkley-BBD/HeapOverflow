package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.Thread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.Optional;

public interface ThreadRepository extends PagingAndSortingRepository<Thread, Integer> {

    Optional<Thread> findById(Integer id);

    Page<Thread> findByTitleContaining(String title, Pageable pageable);
}
