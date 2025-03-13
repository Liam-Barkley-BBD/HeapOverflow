package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Reply;
import com.heapoverflow.api.repositories.ReplyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ReplyController {

    private final ReplyRepository replyRepository;

    public ReplyController(ReplyRepository replyRepository) {
        this.replyRepository = replyRepository;
    }

    @GetMapping("/replies")
    public Page<Reply> getAllReplies(Pageable pageable) {
        return replyRepository.findAll(pageable);
    }

    @GetMapping("/replies/{id}")
    public Optional<Reply> getReplyById(@PathVariable Integer id) {
        return replyRepository.findById(id);
    }

    @GetMapping("/replies/comment/{commentId}")
    public Page<Reply> getRepliesByCommentId(@PathVariable Integer commentId, Pageable pageable) {
        return replyRepository.findByCommentId(commentId, pageable);
    }

    @GetMapping("/replies/user/{userGoogleId}")
    public Page<Reply> getRepliesByUserGoogleId(@PathVariable String userGoogleId, Pageable pageable) {
        return replyRepository.findByUserId(userGoogleId, pageable);
    }
}
