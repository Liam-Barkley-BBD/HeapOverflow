package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {

    List<Reply> findByCommentId(Integer commentId);

    List<Reply> findByUserGoogleId(String userGoogleId);
}
