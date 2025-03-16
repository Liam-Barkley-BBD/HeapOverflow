package com.heapoverflow.api.repositories;

import com.heapoverflow.api.entities.Thread;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ThreadRepository extends JpaRepository<Thread, Integer> {

    Page<Thread> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Thread> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    Page<Thread> findByTitleContainingIgnoreCaseAndDescriptionContainingIgnoreCase(String searchText1, String searchText2, Pageable pageable);

    Page<Thread> findByUser_Id(String userId, Pageable pageable);

    @Query(value = """
            SELECT t.thread_id, t.thread_title, t.thread_description, t.user_google_id, t.created_at, t.closed_at,
                   (SELECT COUNT(thread_upvote_id) FROM thread_upvotes tu WHERE tu.thread_id = t.thread_id) AS threadUpvotesCount
            FROM threads t
            LEFT JOIN (
                SELECT c.thread_id, COUNT(c.comment_id) AS comment_count
                FROM comments c
                GROUP BY c.thread_id
            ) AS cc ON t.thread_id = cc.thread_id
            LEFT JOIN (
                SELECT c.thread_id, COUNT(r.reply_id) AS reply_count
                FROM comments c
                INNER JOIN replies r ON c.comment_id = r.comment_id
                GROUP BY c.thread_id
            ) AS rc ON t.thread_id = rc.thread_id
            WHERE t.closed_at IS NULL
            ORDER BY (
                (SELECT COUNT(thread_upvote_id) FROM thread_upvotes tu WHERE tu.thread_id = t.thread_id) * 1.0 +
                COALESCE(cc.comment_count, 0) * 0.8 +
                COALESCE(rc.reply_count, 0) * 0.5 +
                (1.0 / (EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - t.created_at)) / 86400.0 + 1.0)) * 10.0
            ) DESC
            """, 
            countQuery = """
            SELECT COUNT(*) FROM threads t
            WHERE t.closed_at IS NULL
            """, 
            nativeQuery = true)
    Page<Thread> findTrendingThreads(Pageable pageable);
}
