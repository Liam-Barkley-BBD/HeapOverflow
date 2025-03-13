package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.CommentUpvotes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentUpvotesRepository extends JpaRepository<CommentUpvotes, Integer> {
    
    Optional<CommentUpvotes> findById(Integer id);

    Page<CommentUpvotes> findByCommentId(Integer commentId, Pageable pageable);
}
