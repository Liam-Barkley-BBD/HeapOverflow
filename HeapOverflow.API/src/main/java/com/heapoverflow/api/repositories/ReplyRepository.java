package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {

    Optional<Reply> findById(Integer id);

    Page<Reply> findByComment_Id(Integer commentId, Pageable pageable);

    Page<Reply> findByUser_Id(String userGoogleId, Pageable pageable);
}
