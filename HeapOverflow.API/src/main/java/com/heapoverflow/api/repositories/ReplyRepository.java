package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {

    Optional<Reply> findById(Integer id);

    List<Reply> findByCommentId(Integer commentId);

    List<Reply> findByUserId(String userGoogleId);
}
