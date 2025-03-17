package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.entities.CommentUpvote;
import com.heapoverflow.api.entities.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentUpvoteRepository extends JpaRepository<CommentUpvote, Integer> {
    
    Optional<CommentUpvote> findById(Integer id);

    Page<CommentUpvote> findByComment_Id(Integer commentId, Pageable pageable);

    Page<CommentUpvote> findByUser_Id(String userId, Pageable pageable);

    boolean existsByUserAndComment(User user, Comment comment);

    Optional<CommentUpvote> findByUserAndComment(User user, Comment comment);
}
