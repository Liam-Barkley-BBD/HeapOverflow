package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.CommentUpvote;
import com.heapoverflow.api.entities.ThreadUpvote;
import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.models.CommentUpvoteRequest;
import com.heapoverflow.api.repositories.CommentUpvoteRepository;
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
public class CommentUpvoteService {

    private final CommentUpvoteRepository commentUpvoteRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public CommentUpvoteService(CommentUpvoteRepository commentUpvoteRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.commentUpvoteRepository = commentUpvoteRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public Page<CommentUpvote> getAllCommentUpvotes(Pageable pageable) {
        return commentUpvoteRepository.findAll(pageable);
    }

    public Optional<CommentUpvote> getCommentUpvoteById(Integer id) {
        return commentUpvoteRepository.findById(id);
    }

    public Page<CommentUpvote> getCommentUpvotesByFilter(String userId, Integer commentId, Pageable pageable) {
        if (userId != null) {
            return commentUpvoteRepository.findByUser_Id(userId, pageable);
        } else if (commentId != null) {
            return commentUpvoteRepository.findByComment_Id(commentId, pageable);
        } else {
            return commentUpvoteRepository.findAll(pageable);
        }
    }

    @Transactional
    public CommentUpvote createCommentUpvote(CommentUpvoteRequest commentUpvoteRequest) {
        String authenticatedUserId = AuthUtils.getAuthenticatedUserId();

        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Comment comment = commentRepository.findById(commentUpvoteRequest.getCommentId())
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        CommentUpvote newCommentUpvote = new CommentUpvote(user, comment);
        return commentUpvoteRepository.save(newCommentUpvote);
    }

    @Transactional
    public void deleteCommentUpvote(Integer id) {
        CommentUpvote commentUpvote = commentUpvoteRepository.findById(id)
        .orElseThrow(() -> new ThreadNotFoundException("CommentUpvote with ID " + id + " not found."));

        if (!commentUpvote.getUser().getId().equals(AuthUtils.getAuthenticatedUserId())) {
            throw new UnauthorizedActionException("You do not have permission to delete this comment upvote.");
        }

        commentUpvoteRepository.deleteById(commentUpvote.getId());
    }
}
