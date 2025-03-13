package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.entities.Reply;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.models.ReplyRequest;
import com.heapoverflow.api.repositories.CommentRepository;
import com.heapoverflow.api.repositories.ReplyRepository;
import com.heapoverflow.api.repositories.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ReplyController {

    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public ReplyController(ReplyRepository replyRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.replyRepository = replyRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
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

    @PostMapping("/replies")
    public ResponseEntity<?> createThread(@RequestBody ReplyRequest replyRequest) {

        Optional<User> user = userRepository.findById(replyRequest.getUserId());

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"User not found\"}");
        }

        Optional<Comment> comment = commentRepository.findById(replyRequest.getCommentdId());

        if (comment.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Comment ID not found\"}");
        }

        var newReply = new Reply(replyRequest.getContent(), user.get(), comment.get());
        return ResponseEntity.ok(replyRepository.save(newReply));
    }
}
