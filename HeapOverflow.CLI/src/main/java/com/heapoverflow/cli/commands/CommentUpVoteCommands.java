package com.heapoverflow.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.CommentsServices;
import com.heapoverflow.cli.services.ThreadsService;
import com.heapoverflow.cli.utils.EnvUtils;

@ShellComponent
public class CommentUpVoteCommands {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ShellMethod(key = "delete-comment-up-vote", value = "Delete your comment up vote")
    public String deleteCommentUpload(
            @ShellOption(value = "id", help = "The comment up vote you wish to delete", defaultValue = "") String id) {
        if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return "You are not logged, please login!";
        } else if (id.equals("")) {
            return "the commentId must be specified like: \"delete-comment-up-vote --id {id_value}\"";
        } else {
            try {

                ThreadsService.deleteThread(id);
                return String.format("Your comment up vote %s has been deleted", id);
            } catch (Exception error) {
                return error.getMessage();
            }
        }
    }

    @ShellMethod(key = "post-comment-up-vote", value = "Upvote a comment")
    public String postThreadUpVote(
            @ShellOption(value = "commentId", help = "The ID of the comment being upvoted") String commentId) {

        if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return "You are not logged in, please login!";
        }

        String userId = EnvUtils.retrieveValue(EnvConstants.GOOGLE_SUB);

        if (userId == null || userId.isBlank() || commentId == null || commentId.isBlank()) {
            return "Both userId and commentId must be specified like: \"post-thread-up-vote --userId {user_id} --threadId {thread_id}\"";
        }

        try {
            JsonNode response = CommentsServices.postComment(userId, commentId);
            return "Comment upvote successful: " + response.toString();
        } catch (Exception error) {
            return "Error upvoting thread: " + error.getMessage();
        }
    }
}
