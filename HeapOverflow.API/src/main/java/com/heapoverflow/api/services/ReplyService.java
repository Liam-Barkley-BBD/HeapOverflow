package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.Reply;
import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.models.ReplyRequest;
import com.heapoverflow.api.repositories.ReplyRepository;
import com.heapoverflow.api.repositories.CommentRepository;
import com.heapoverflow.api.repositories.UserRepository;
import com.heapoverflow.api.exceptions.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public ReplyService(ReplyRepository replyRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.replyRepository = replyRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public Page<Reply> getAllReplies(Pageable pageable) {
        return replyRepository.findAll(pageable);
    }

    public Optional<Reply> getReplyById(Integer id) {
        return replyRepository.findById(id);
    }

    public Page<Reply> getRepliesByUserId(String id, Pageable pageable) {
        return replyRepository.findByUserId(id, pageable);
    }

    public Page<Reply> getRepliesByCommentId(Integer id, Pageable pageable) {
        return replyRepository.findByCommentId(id, pageable);
    }

    public Reply createReply(ReplyRequest replyRequest) {
        User user = userRepository.findById(replyRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Comment comment = commentRepository.findById(replyRequest.getCommentId())
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        Reply newReply = new Reply(replyRequest.getContent(), user, comment);
        return replyRepository.save(newReply);
    }
}
