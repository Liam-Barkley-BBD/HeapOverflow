package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.Thread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThreadRepository extends JpaRepository<Thread, Integer> {

    List<Thread> findByTitleContaining(String title);
}
