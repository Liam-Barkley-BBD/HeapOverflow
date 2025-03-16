package com.heapoverflow.api.services;

import com.heapoverflow.api.entities.CommentUpvote;
import com.heapoverflow.api.entities.Comment;
import com.heapoverflow.api.entities.User;
import com.heapoverflow.api.models.CommentUpvoteRequest;
import com.heapoverflow.api.repositories.CommentUpvoteRepository;
import com.heapoverflow.api.repositories.CommentRepository;
import com.heapoverflow.api.repositories.UserRepository;
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

    public Page<CommentUpvote> getCommentUpvotesByUserId(String id, Pageable pageable) {
        return commentUpvoteRepository.findByUserId(id, pageable);
    }

    public Page<CommentUpvote> getCommentUpvotesByCommentId(Integer id, Pageable pageable) {
        return commentUpvoteRepository.findByCommentId(id, pageable);
    }

    @Transactional
    public CommentUpvote createCommentUpvote(CommentUpvoteRequest commentUpvoteRequest) {
        User user = userRepository.findById(commentUpvoteRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Comment comment = commentRepository.findById(commentUpvoteRequest.getCommentId())
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        CommentUpvote newCommentUpvote = new CommentUpvote(user, comment);
        return commentUpvoteRepository.save(newCommentUpvote);
    }

    @Transactional
    public void deleteCommentUpvote(Integer id) {
        if (!commentUpvoteRepository.existsById(id)) {
            throw new CommentUpvoteNotFoundException("CommentUpvote with ID " + id + " not found.");
        }
        commentUpvoteRepository.deleteById(id);
    }
}
