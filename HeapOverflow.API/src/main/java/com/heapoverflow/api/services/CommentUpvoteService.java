package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.CommentUpvote;
import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.repositories.CommentUpvoteRepository;
import com.heapoverflow.api.repositories.CommentRepository;
import com.heapoverflow.api.repositories.UserRepository;
import com.heapoverflow.api.utils.AuthUtils;
import com.heapoverflow.api.exceptions.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public CommentUpvote createCommentUpvote(Integer commentId) {
        String authenticatedUserId = AuthUtils.getAuthenticatedUserId();

        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));
            
        if (commentUpvoteRepository.existsByUserAndComment(user, comment)) {
            throw new IllegalStateException("User has already upvoted the comment");
        }

        CommentUpvote newCommentUpvote = new CommentUpvote(user, comment);
        return commentUpvoteRepository.save(newCommentUpvote);
    }

    @Transactional
    public void deleteCommentUpvote(Integer commentId) {
        String authenticatedUserId = AuthUtils.getAuthenticatedUserId();

        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentNotFoundException("Comment not found"));
        
        CommentUpvote commentUpvote = commentUpvoteRepository.findByUserAndComment(user, comment)
            .orElseThrow(() -> new CommentUpvoteNotFoundException("CommentUpvote with comment ID: " + commentId + " for user not found"));

        commentUpvoteRepository.deleteById(commentUpvote.getId());
    }
}
