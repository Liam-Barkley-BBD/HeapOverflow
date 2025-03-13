package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.CommentUpvote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentUpvoteRepository extends JpaRepository<CommentUpvote, Integer> {
    
    Optional<CommentUpvote> findById(Integer id);

    Page<CommentUpvote> findByCommentId(Integer commentId, Pageable pageable);
}
