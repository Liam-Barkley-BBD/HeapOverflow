package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.Reply;
import com.heapoverflow.api.entities.ThreadUpvote;
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

    public Page<Reply> getRepliesByFilter(String userId, Integer commentId, Pageable pageable) {
        if (userId != null) {
            return replyRepository.findByUser_Id(userId, pageable);
        } else if (commentId != null) {
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

        Reply newReply = new Reply(replyRequest.getContent(), user, comment);
        return replyRepository.save(newReply);
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
