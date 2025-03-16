package com.heapoverflow.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.ThreadsService;
import com.heapoverflow.cli.utils.EnvUtils;

@ShellComponent
public class ThreadsUpVoteCommands {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ShellMethod(key = "delete-tread-up-vote", value = "Delete your tread up vote")
    public String deleteThreadUpVote(
            @ShellOption(value = "id", help = "The tread up vote you wish to delete", defaultValue = "") String id) {
        if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return "You are not logged, please login!";
        } else if (id.equals("")) {
            return "the treadId must be specified like: \"delete-tread-up-vote --id {id_value}\"";
        } else {
            try {

                ThreadsService.deleteThread(id);
                return String.format("Your tread up vote %s has been deleted", id);
            } catch (Exception error) {
                return error.getMessage();
            }
        }
    }

    @ShellMethod(key = "post-thread-up-vote", value = "Upvote a thread")
    public String postThreadUpVote(
            @ShellOption(value = "threadId", help = "The ID of the thread being upvoted") String threadId) {

        if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return "You are not logged in, please login!";
        }

        String userId = EnvUtils.retrieveValue(EnvConstants.GOOGLE_SUB);

        if (userId == null || userId.isBlank() || threadId == null || threadId.isBlank()) {
            return "Both userId and threadId must be specified like: \"post-thread-up-vote --userId {user_id} --threadId {thread_id}\"";
        }

        try {
            JsonNode response = ThreadsService.postThread(userId, threadId);
            return "Thread upvote successful: " + response.toString();
        } catch (Exception error) {
            return "Error upvoting thread: " + error.getMessage();
        }
    }
}
