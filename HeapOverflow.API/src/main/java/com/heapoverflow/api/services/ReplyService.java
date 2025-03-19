package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.Reply;
import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.models.ReplyRequest;
import com.heapoverflow.api.repositories.ReplyRepository;
import com.heapoverflow.api.repositories.CommentRepository;
import com.heapoverflow.api.repositories.UserRepository;
import com.heapoverflow.api.utils.AuthUtils;
import com.heapoverflow.api.exceptions.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Page<Reply> getRepliesByFilter(Integer commentId, Pageable pageable) {
        if (commentId != null) {
            return replyRepository.findByComment_Id(commentId, pageable);
        } else {
            return replyRepository.findAll(pageable);
        }
    }

    @Transactional
    public Reply createReply(ReplyRequest replyRequest) {
        String authenticatedUserId = AuthUtils.getAuthenticatedUserId();

        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Comment comment = commentRepository.findById(replyRequest.getCommentId())
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        if (comment.getThread().getClosedAt() != null) {
            throw new IllegalStateException("Thread is already closed.");
        }

        if (replyRequest.getContent() == null || replyRequest.getContent().trim().isEmpty()) {
            throw new BadRequestException("REply cannot be empty.");
        }

        Reply newReply = new Reply(replyRequest.getContent(), user, comment);
        return replyRepository.save(newReply);
    }

    @Transactional
    public Reply updateReply(Integer replyId, String content) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new ReplyNotFoundException("Reply with ID " + replyId + " not found."));
        
        if (!reply.getUser().getId().equals(AuthUtils.getAuthenticatedUserId())) {
            throw new UnauthorizedActionException("You do not have permission to update this reply.");
        }
    
        if (reply.getComment().getThread().getClosedAt() != null) {
            throw new IllegalStateException("Thread is already closed.");
        }

        if (content == null || content.trim().isEmpty()) {
            throw new BadRequestException("REply cannot be empty.");
        }
    
        reply.setContent(content);
        return replyRepository.save(reply);
    }

    @Transactional
    public void deleteReply(Integer id) {

        Reply reply = replyRepository.findById(id)
        .orElseThrow(() -> new ReplyNotFoundException("Reply with ID " + id + " not found."));

        if (!reply.getUser().getId().equals(AuthUtils.getAuthenticatedUserId())) {
            throw new UnauthorizedActionException("You do not have permission to delete this reply.");
        }

        replyRepository.deleteById(reply.getId());
    }
}
