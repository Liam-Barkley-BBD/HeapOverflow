package com.heapoverflow.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.CommentUpVotesService;
import com.heapoverflow.cli.utils.EnvUtils;

@ShellComponent
public class CommentUpVoteCommands {

    @ShellMethod(key = "delete-comment-up-vote", value = "Delete your comment up vote")
    public String deleteCommentUpload(
            @ShellOption(value = "id", help = "The comment up vote you wish to delete", defaultValue = "") int id) {
        if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return "You are not logged, please login!";
        } else {
            try {

                CommentUpVotesService.deleteCommentUpVote(id);
                return String.format("Your comment up vote %s has been deleted", id);
            } catch (Exception error) {
                return error.getMessage();
            }
        }
    }

    @ShellMethod(key = "post-comment-up-vote", value = "Upvote a comment")
    public String postThreadUpVote(
            @ShellOption(value = "commentId", help = "The ID of the comment being upvoted") int commentId) {

        if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return "You are not logged in, please login!";
        }

        try {
            JsonNode response = CommentUpVotesService.postCommentUpVote(commentId);
            return "Comment upvote successful: " + response.toString();
        } catch (Exception error) {
            return "Error upvoting thread: " + error.getMessage();
        }
    }
}
