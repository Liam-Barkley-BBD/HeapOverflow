package com.heapoverflow.cli.commands;

import java.util.List;
import java.util.Optional;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.TableModelBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.ReplyServices;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.FlagsCheckUtils;
import com.heapoverflow.cli.utils.TextUtils;

@ShellComponent
public class ReplyCommands {

    @ShellMethod(key = "replies", value = "Manage replies in the system")
    public String replies(
        @ShellOption(value = "list", help = "List replies", defaultValue = "false") boolean list,
        @ShellOption(value = "get", help = "Get a specific reply", defaultValue = "false") boolean get,
        @ShellOption(value = "post", help = "Post a new reply", defaultValue = "false") boolean post,
        @ShellOption(value = "edit", help = "Edit a reply", defaultValue = "false") boolean edit,
        @ShellOption(value = "delete", help = "Delete a reply", defaultValue = "false") boolean delete,
        @ShellOption(value = "replyId", help = "Reply ID", defaultValue = ShellOption.NULL) Optional<String> replyId,
        @ShellOption(value = "content", help = "Reply content", defaultValue = ShellOption.NULL) Optional<String> content,
        @ShellOption(value = "commentId", help = "Comment ID", defaultValue = ShellOption.NULL) Optional<String> commentId,
        @ShellOption(value = "page", help = "Page number", defaultValue = ShellOption.NULL) Optional<Integer> page,
        @ShellOption(value = "size", help = "Page size", defaultValue = ShellOption.NULL) Optional<Integer> size
    ) {
        List<String> selectedFlags = FlagsCheckUtils.ensureOnlyOneFlagIsSetForReplies(list, get, post, edit, delete);
        if(selectedFlags.size() > 1){
            return "You cannot use multiple action based flags at once: " + selectedFlags.toString();
        } else if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return "You are not logged in. Please log in!";
        } else if (list) {
            return listReplies(page.orElse(1), size.orElse(10), commentId.orElse(""));
        } else if (get) {
            return getReplyById(replyId.orElse(""));
        } else if (post) {
            return postReply(content.orElse(""), commentId.orElse(""));
        } else if (edit) {
            return editReply(replyId.orElse(""), content.orElse(""));
        } else if (delete) {
            return deleteReply(replyId.orElse(""));
        } else {
            return "Invalid command. Use: \n" +
                                        "\t\t\t--list --commentId[optional] {id} --page[optional] {num} --size[optional] {num}\n" +
                                        "\t\t\t--get --replyId {id}\n" + 
                                        "\t\t\t--post --content \"text\" --commentId {id}\n" +
                                        "\t\t\t--edit --replyId {id} --content \"text\"\n" +
                                        "\t\t\t--delete --replyId {id}\n" +
                                        "\t\t\t--help";
        }
    }

    public static String listReplies(int page, int size, String commentId) {
        try {
            JsonNode jsonResponse = ReplyServices.getReplies(Math.max(0, page - 1), size, commentId);
            JsonNode replyNode = jsonResponse.path("content");
            if (!replyNode.isArray() || replyNode.isEmpty()) {
                return "No replies found.";
            } else{
                int totalThreads = jsonResponse.path("totalElements").asInt(0);
                int totalPages = jsonResponse.path("totalPages").asInt(1);
                int currentPage = jsonResponse.path("number").asInt(0) + 1;
                boolean isLastPage = jsonResponse.path("last").asBoolean();
                
                return TextUtils.renderTable(buildReplyTable(replyNode).build()) +
                    String.format("\nPage %d of %d | Total Replies: %d %s", currentPage, totalPages, totalThreads, isLastPage ? "(Last Page)" : "");
            }
        } catch (Exception e) {
            return "Error retrieving replies: " + e.getMessage();
        }
    }

    private String getReplyById(String replyId) {
        if (replyId.isEmpty()) {
            return "The ID must be specified like: replies --get --replyId {id}";
        }
        try {
            JsonNode reply = ReplyServices.getReplyById(replyId);
            return TextUtils.renderTable(buildReplyTable(reply).build());
        } catch (Exception e) {
            return "Error retrieving reply: " + e.getMessage();
        }
    }

    private String postReply(String content, String commentId) {
        if (commentId.isEmpty()) {
            return "The commentId must be specified: replies --post --content \"{content}\" --commentId {commentId}";
        }
        try {
            JsonNode reply = ReplyServices.postReply(content, commentId);
            return TextUtils.renderTable(buildReplyTable(reply).build());
        } catch (Exception e) {
            return "Error posting reply: " + e.getMessage();
        }
    }

    private String editReply(String replyId, String content) {
        if (replyId.isEmpty()) {
            return "The ID must be specified: replies --edit --replyId {id} --content \"{content}\"";
        }
        try {
            JsonNode reply = ReplyServices.patchReply(content, replyId);
            return TextUtils.renderTable(buildReplyTable(reply).build());
        } catch (Exception e) {
            return "Error editing reply: " + e.getMessage();
        }
    }

    private String deleteReply(String replyId) {
        if (replyId.isEmpty()) {
            return "The ID must be specified: replies --delete --replyId {id}";
        }
        try {
            ReplyServices.deleteReply(replyId);
            return "Reply with ID " + replyId + " has been deleted.";
        } catch (Exception e) {
            return "Error deleting reply: " + e.getMessage();
        }
    }

    private static TableModelBuilder<String> buildReplyTable(JsonNode replyNode) {
        TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
        modelBuilder.addRow().addValue("ID").addValue("Content")
                .addValue("User").addValue("Email")
                .addValue("Created At").addValue("CommentId");
        if (replyNode.isArray()) {
            for (JsonNode reply : replyNode) {
                modelBuilder.addRow().addValue(reply.path("id").asText("N/A"))
                        .addValue(reply.path("content").asText("N/A"))
                        .addValue(reply.path("user").path("username").asText("N/A"))
                        .addValue(reply.path("user").path("email").asText("N/A"))
                        .addValue(reply.path("createdAt").asText("N/A"))
                        .addValue(reply.path("commentId").asText("N/A"));
            }
        } else {
            modelBuilder.addRow().addValue(replyNode.path("id").asText("N/A"))
                    .addValue(replyNode.path("content").asText("N/A"))
                    .addValue(replyNode.path("user").path("username").asText("N/A"))
                    .addValue(replyNode.path("user").path("email").asText("N/A"))
                    .addValue(replyNode.path("createdAt").asText("N/A"))
                    .addValue(replyNode.path("commentId").asText("N/A"));
        }
        return modelBuilder;
    }
}
