package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.Thread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ThreadRepository extends JpaRepository<Thread, Integer> {

    Optional<Thread> findById(Integer id);

    List<Thread> findByTitleContaining(String title);
}
