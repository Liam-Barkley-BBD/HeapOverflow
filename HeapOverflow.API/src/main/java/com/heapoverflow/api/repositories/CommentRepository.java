package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    Optional<Comment> findById(Integer id);

    List<Comment> findByThreadId(Integer threadId);

    List<Comment> findByUserGoogleId(String userGoogleId);
}
