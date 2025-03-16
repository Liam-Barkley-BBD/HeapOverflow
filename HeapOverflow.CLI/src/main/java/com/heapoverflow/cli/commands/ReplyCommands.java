package com.heapoverflow.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.TableModelBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.ReplyServices;
import com.heapoverflow.cli.utils.EnvUtils;
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
        @ShellOption(value = "id", help = "Reply ID", defaultValue = "") String id,
        @ShellOption(value = "content", help = "Reply content", defaultValue = "") String content,
        @ShellOption(value = "commentId", help = "Comment ID", defaultValue = "") String commentId,
        @ShellOption(value = "page", help = "Page number", defaultValue = "0") int page,
        @ShellOption(value = "size", help = "Page size", defaultValue = "5") int size
    ) {
        if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return "You are not logged in. Please log in!";
        } else if (list) {
            return listReplies(page, size);
        } else if (get) {
            return getReplyById(id);
        } else if (post) {
            return postReply(content, commentId);
        } else if (edit) {
            return editReply(id, content);
        } else if (delete) {
            return deleteReply(id);
        } else {
            return "Invalid command. Use --list, --get --id {id}, --post --content \"text\" --commentId {id}, --edit --id {id} --content \"text\", or --delete --id {id}.";
        }
    }

    private String listReplies(int page, int size) {
        try {
            JsonNode jsonResponse = ReplyServices.getReplies(Math.max(0, page - 1), size);
            JsonNode contentArray = jsonResponse.path("content");
            if (!contentArray.isArray() || contentArray.isEmpty()) {
                return "No replies found.";
            } else{
                TableModelBuilder<String> modelBuilder = buildReplyTable(contentArray);

                int totalThreads = jsonResponse.path("totalElements").asInt(0);
                int totalPages = jsonResponse.path("totalPages").asInt(1);
                int currentPage = jsonResponse.path("number").asInt(0) + 1;
                boolean isLastPage = jsonResponse.path("last").asBoolean();
                
                return TextUtils.renderTable(modelBuilder.build()) +
                    String.format("\nPage %d of %d | Total Replies: %d %s", currentPage, totalPages, totalThreads, isLastPage ? "(Last Page)" : "");
            }
        } catch (Exception e) {
            return "Error retrieving replies: " + e.getMessage();
        }
    }

    private String getReplyById(String id) {
        if (id.isEmpty()) {
            return "The ID must be specified like: replies --get --id {id}";
        }
        try {
            JsonNode reply = ReplyServices.getReplyById(id);
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

    private String editReply(String id, String content) {
        if (id.isEmpty()) {
            return "The ID must be specified: replies --edit --id {id} --content \"{content}\"";
        }
        try {
            JsonNode reply = ReplyServices.patchReply(content, id);
            return TextUtils.renderTable(buildReplyTable(reply).build());
        } catch (Exception e) {
            return "Error editing reply: " + e.getMessage();
        }
    }

    private String deleteReply(String id) {
        if (id.isEmpty()) {
            return "The ID must be specified: replies --delete --id {id}";
        }
        try {
            ReplyServices.deleteReply(id);
            return "Reply with ID " + id + " has been deleted.";
        } catch (Exception e) {
            return "Error deleting reply: " + e.getMessage();
        }
    }

    private TableModelBuilder<String> buildReplyTable(JsonNode replyNode) {
        TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
        modelBuilder.addRow().addValue("ID").addValue("Content")
                .addValue("GID").addValue("User").addValue("Email")
                .addValue("Created At").addValue("CommentId");
        if (replyNode.isArray()) {
            for (JsonNode reply : replyNode) {
                modelBuilder.addRow().addValue(reply.path("id").asText("N/A"))
                        .addValue(reply.path("content").asText("N/A"))
                        .addValue(reply.path("user").path("id").asText("N/A"))
                        .addValue(reply.path("user").path("username").asText("N/A"))
                        .addValue(reply.path("user").path("email").asText("N/A"))
                        .addValue(reply.path("createdAt").asText("N/A"))
                        .addValue(reply.path("commentId").asText("N/A"));
            }
        } else {
            modelBuilder.addRow().addValue(replyNode.path("id").asText("N/A"))
                    .addValue(replyNode.path("content").asText("N/A"))
                    .addValue(replyNode.path("user").path("id").asText("N/A"))
                    .addValue(replyNode.path("user").path("username").asText("N/A"))
                    .addValue(replyNode.path("user").path("email").asText("N/A"))
                    .addValue(replyNode.path("createdAt").asText("N/A"))
                    .addValue(replyNode.path("commentId").asText("N/A"));
        }
        return modelBuilder;
    }
}
