package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    Optional<Comment> findById(Integer id);

    Page<Comment> findByThread_Id(Integer threadId, Pageable pageable);
}
