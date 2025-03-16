package com.heapoverflow.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.TableModelBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.CommentsServices;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.TextUtils;

@ShellComponent
public class CommentCommands {
    
    @ShellMethod(key = "comment", value = "Manage comments in the system")
    public String comment(
        @ShellOption(value = "list", help = "List replies", defaultValue = "false") boolean list,
        @ShellOption(value = "get", help = "Get a specific reply", defaultValue = "false") boolean get,
        @ShellOption(value = "post", help = "Post a new reply", defaultValue = "false") boolean post,
        @ShellOption(value = "edit", help = "Edit a reply", defaultValue = "false") boolean edit,
        @ShellOption(value = "delete", help = "Delete a reply", defaultValue = "false") boolean delete,
        @ShellOption(value = "id", help = "Comment ID", defaultValue = "") String id,
        @ShellOption(value = "content", help = "Comment content", defaultValue = "") String content,
        @ShellOption(value = "threadId", help = "Thread ID", defaultValue = "") String threadId,
        @ShellOption(value = "page", help = "Page number", defaultValue = "1") int page,
        @ShellOption(value = "size", help = "Page size", defaultValue = "5") int size
    ) {
        if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return "You are not logged in. Please log in!";
        } else if (list) {
            return getAllComments(page, size);
        } else if (get) {
            return getComment(id);
        } else if (post) {
            return postComment(content, threadId);
        } else if (edit) {
            return editComment(id, content);
        } else if (delete) {
            return deleteComment(id);
        } else {
            return "Invalid command. Use --list, --get --id {id}, --post --content \"text\" --threadId {id}, --edit --id {id} --content \"text\", or --delete --id {id}.";
        }
    }

    private String getAllComments(int page, int size) {
        try {
            JsonNode jsonResponse = CommentsServices.getComments(Math.max(0, page - 1), size);
            JsonNode contentArray = jsonResponse.path("content");

            if (!contentArray.isArray() || contentArray.isEmpty()) {
                return "No comments found.";
            } else{
                TableModelBuilder<String> modelBuilder = buildReplyTable(contentArray);

                int totalThreads = jsonResponse.path("totalElements").asInt(0);
                int totalPages = jsonResponse.path("totalPages").asInt(1);
                int currentPage = jsonResponse.path("number").asInt(0) + 1;
                boolean isLastPage = jsonResponse.path("last").asBoolean();
                
                return TextUtils.renderTable(modelBuilder.build()) +
                    String.format("\nPage %d of %d | Total Comments: %d %s", currentPage, totalPages, totalThreads, isLastPage ? "(Last Page)" : "");
            }
        } catch (Exception e) {
            return "Error retrieving comments: " + e.getMessage();
        }
    }

    private String getComment(String id) {
        if (id.isEmpty()) {
            return "The id must be specified like: 'comment get --id {id_value}'";
        } else{
            try {
                JsonNode reply = CommentsServices.getCommentById(id);
                return TextUtils.renderTable(buildReplyTable(reply).build());
            } catch (Exception e) {
                return "Error retrieving comment: " + e.getMessage();
            }
        }
    }

    private String postComment(String content, String threadId) {
        if (threadId.isEmpty()) {
            return "The threadId must be specified like: 'comment post --content \"{content_value}\" --threadId {threadId_value}'";
        } else{
            try {
                JsonNode reply = CommentsServices.postComment(content, threadId);
                return TextUtils.renderTable(buildReplyTable(reply).build());
            } catch (Exception e) {
                return "Error posting comment: " + e.getMessage();
            }
        }
    }

    private String editComment(String id, String content) {
        if (id.isEmpty()) {
            return "The id must be specified like: 'comment edit --id {id_value} --content \"{content_value}\"'";
        } else{
            try {
                JsonNode reply = CommentsServices.patchComment(content, id);
                return TextUtils.renderTable(buildReplyTable(reply).build());
            } catch (Exception e) {
                return "Error editing comment: " + e.getMessage();
            }
        }
    }

    private String deleteComment(String id) {
        if (id.isEmpty()) {
            return "The id must be specified like: 'comment delete --id {id_value}'";
        } else{
            try {
                CommentsServices.deleteComment(id);
                return String.format("Your comment with id %s has been deleted", id);
            } catch (Exception e) {
                return "Error deleting comment: " + e.getMessage();
            }
        }
    }

    private TableModelBuilder<String> buildReplyTable(JsonNode commentNode) {
        TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
        modelBuilder.addRow().addValue("ID").addValue("Content")
            .addValue("GID").addValue("User").addValue("Email")
            .addValue("Created At").addValue("Upvotes").addValue("ThreadId");
        if (commentNode.isArray()) {
            for (JsonNode comment : commentNode) {
                JsonNode userNode = comment.path("user");
                modelBuilder.addRow()
                    .addValue(comment.path("id").asText("N/A"))
                    .addValue(comment.path("content").asText("N/A"))
                    .addValue(userNode.path("id").asText("N/A"))
                    .addValue(userNode.path("username").asText("N/A"))
                    .addValue(userNode.path("email").asText("N/A"))
                    .addValue(comment.path("createdAt").asText("N/A"))
                    .addValue(comment.path("commentUpvotesCount").asText("N/A"))
                    .addValue(comment.path("threadId").asText("N/A"));
            }
        } else {
            JsonNode userNode = commentNode.path("user");
            modelBuilder.addRow()
                        .addValue(commentNode.path("id").asText("N/A"))
                        .addValue(commentNode.path("content").asText("N/A"))
                        .addValue(userNode.path("id").asText("N/A"))
                        .addValue(userNode.path("username").asText("N/A"))
                        .addValue(userNode.path("email").asText("N/A"))
                        .addValue(commentNode.path("createdAt").asText("N/A"))
                        .addValue(commentNode.path("commentUpvotesCount").asText("N/A"))
                        .addValue(commentNode.path("threadId").asText("N/A"));
        }
        return modelBuilder;
    }
}
