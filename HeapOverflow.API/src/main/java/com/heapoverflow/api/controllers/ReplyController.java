package com.heapoverflow.api.controllers;

import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.entities.Reply;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.repositories.CommentRepository;
import com.heapoverflow.api.repositories.ReplyRepository;
import com.heapoverflow.api.repositories.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ReplyController {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public ReplyController(ReplyRepository replyRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.replyRepository = replyRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
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

    /** POST endpoints */
    @PostMapping("/replies")
    public ResponseEntity<?> createReply(@RequestBody Reply reply) {
        Comment comment = commentRepository.findById(reply.getComment().getId())
                .orElse(null);
        User user = userRepository.findById(reply.getUser().getId())
                .orElse(null);
 
        if (comment == null) {
            return ResponseEntity.badRequest().body("{\"error\": \"Comment does not exist\"}");
        }
        else if (user == null) {
            return ResponseEntity.badRequest().body("{\"error\": \"User does not exist\"}");
        }
        else {
            reply.setComment(comment);
            reply.setUser(user);
            reply.setCreatedAt(LocalDateTime.now());
            
            return ResponseEntity.ok(replyRepository.save(reply));
        }
    }
}
