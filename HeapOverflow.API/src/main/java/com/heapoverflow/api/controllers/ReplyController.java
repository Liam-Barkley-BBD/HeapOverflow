package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Reply;
import com.heapoverflow.api.repositories.ReplyRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ReplyController {

    private final ReplyRepository replyRepository;

    public ReplyController(ReplyRepository replyRepository) {
        this.replyRepository = replyRepository;
    }

    @GetMapping("/replies")
    public List<Reply> getAllReplies() {
        return replyRepository.findAll();
    }

    @GetMapping("/replies/{id}")
    public Optional<Reply> getReplyById(@PathVariable Integer id) {
        return replyRepository.findById(id);
    }

    @GetMapping("/replies/comment/{commentId}")
    public List<Reply> getRepliesByCommentId(@PathVariable Integer commentId) {
        return replyRepository.findByCommentId(commentId);
    }

    @GetMapping("/replies/user/{userGoogleId}")
    public List<Reply> getRepliesByUserGoogleId(@PathVariable String userGoogleId) {
        return replyRepository.findByUserId(userGoogleId);
    }
}
