package com.heapoverflow.cli.commands;

import java.util.List;
import java.util.Optional;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.TableModelBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.CommentUpVotesService;
import com.heapoverflow.cli.services.CommentsServices;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.FlagsCheckUtils;
import com.heapoverflow.cli.utils.TextUtils;

@ShellComponent
public class CommentCommands {

    @ShellMethod(key = "comment", value = "Manage comments in the system")
    public String comment(
            @ShellOption(value = "list", help = "List comments", defaultValue = "false") boolean list,
            @ShellOption(value = "get", help = "Get a specific comment", defaultValue = "false") boolean get,
            @ShellOption(value = "post", help = "Post a new comment", defaultValue = "false") boolean post,
            @ShellOption(value = "edit", help = "Edit a comment", defaultValue = "false") boolean edit,
            @ShellOption(value = "delete", help = "Delete a comment", defaultValue = "false") boolean delete,
            @ShellOption(value = "upvote", help = "Upvote a comment", defaultValue = "false") boolean upvote,
            @ShellOption(value = "un-upvote", help = "Remove your upvote to a comment", defaultValue = "false") boolean unupvote,
            @ShellOption(value = "commentId", help = "Comment ID", defaultValue = ShellOption.NULL) Optional<String> commentId,
            @ShellOption(value = "content", help = "Comment content", defaultValue = ShellOption.NULL) Optional<String> content,
            @ShellOption(value = "threadId", help = "Thread ID", defaultValue = ShellOption.NULL) Optional<String> threadId,
            @ShellOption(value = "page", help = "Page number", defaultValue = "1") Integer page,
            @ShellOption(value = "size", help = "Page size", defaultValue = "10") Integer size
    ) {
        List<String> selectedFlags = FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(list, get, post, edit, delete, upvote, unupvote);
        if(selectedFlags.size() > 1){
            return "You cannot use multiple action based flags at once: " + selectedFlags.toString();
        } else if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return "You are not logged in. Please log in!";
        } else if (list) {
            return getAllComments(page, size, threadId.orElse(""));
        } else if (get) {
            return getComment(commentId.orElse("")) + 
                "\nReplies for comments: \n" +
            ReplyCommands.listReplies(page, size, commentId.orElse(""));
        } else if (post) {
            return postComment(content.orElse(""), threadId.orElse(""));
        } else if (edit) {
            return editComment(commentId.orElse(""), content.orElse(""));
        } else if (delete) {
            return deleteComment(commentId.orElse(""));
        } else if (upvote) {
            return upvoteComment(commentId.orElse(""));
        } else if (unupvote) {
            return unupvoteComment(commentId.orElse(""));
        } else {
            return "Invalid command. Use: \n" +
                    "\t\t\t--list --threadId[optional] {id} --page[optional] {num} --size[optional] {num}\n" +
                    "\t\t\t--get --commentId {id} --page[optional] {num} --size[optional] {num}\n" +
                    "\t\t\t--post --content \"text\" --threadId {id}\n" +
                    "\t\t\t--edit --commentId {id} --content \"text\"\n" +
                    "\t\t\t--delete --commentId {id}\n" +
                    "\t\t\t--upvote --commentId {id}\n" +
                    "\t\t\t--un-upvote --commentId {id}\n" +
                    "\t\t\t--help";
        }
    }

    public static String getAllComments(int page, int size, String threadId) {
        try {
            JsonNode jsonResponse = CommentsServices.getComments(Math.max(0, page - 1), size, threadId);
            JsonNode commentNode = jsonResponse.path("content");

            if (!commentNode.isArray() || commentNode.isEmpty()) {
                return "No comments found.";
            } else {
                int totalThreads = jsonResponse.path("totalElements").asInt(0);
                int totalPages = jsonResponse.path("totalPages").asInt(1);
                int currentPage = jsonResponse.path("number").asInt(0) + 1;
                boolean isLastPage = jsonResponse.path("last").asBoolean();

                return TextUtils.renderTable(buildCommentTable(commentNode).build()) +
                        String.format("\nPage %d of %d | Total Comments: %d %s", currentPage, totalPages, totalThreads,
                                isLastPage ? "(Last Page)" : "");
            }
        } catch (Exception e) {
            return "Error retrieving comments: " + e.getMessage();
        }
    }

    private String getComment(String id) {
        if (id.isEmpty()) {
            return "The id must be specified like: 'comment get --commentId {id_value}'";
        } else {
            try {
                JsonNode comment = CommentsServices.getCommentById(id);
                return TextUtils.renderTable(buildCommentTable(comment).build());
            } catch (Exception e) {
                return "Error retrieving comment: " + e.getMessage();
            }
        }
    }

    private String postComment(String content, String threadId) {
        if (threadId.isEmpty()) {
            return "The threadId must be specified like: 'comment post --content \"{content_value}\" --threadId {threadId_value}'";
        } else {
            try {
                JsonNode comment = CommentsServices.postComment(content, threadId);
                return TextUtils.renderTable(buildCommentTable(comment).build());
            } catch (Exception e) {
                return "Error posting comment: " + e.getMessage();
            }
        }
    }

    private String editComment(String commentId, String content) {
        if (commentId.isEmpty()) {
            return "The id must be specified like: 'comment edit --commentId {id_value} --content \"{content_value}\"'";
        } else {
            try {
                JsonNode comment = CommentsServices.patchComment(content, commentId);
                return TextUtils.renderTable(buildCommentTable(comment).build());
            } catch (Exception e) {
                return "Error editing comment: " + e.getMessage();
            }
        }
    }

    private String deleteComment(String commentId) {
        if (commentId.isEmpty()) {
            return "The id must be specified like: 'comment delete --commentId {id_value}'";
        } else {
            try {
                CommentsServices.deleteComment(commentId);
                return String.format("Your comment with commentId %s has been deleted", commentId);
            } catch (Exception e) {
                return "Error deleting comment: " + e.getMessage();
            }
        }
    }

    private String upvoteComment(String commentId) {
        if (commentId.isEmpty()) {
            return "The id must be specified like: 'comment upvote --commentId {id_value}'";
        } else {
            try {
                CommentUpVotesService.postCommentUpVote(commentId);
                return String.format("You upvoted a comment with commentId %s", commentId);
            } catch (Exception e) {
                return "Error upvoting comment: " + e.getMessage();
            }
        }
    }

    private String unupvoteComment(String commentId) {
        if (commentId.isEmpty()) {
            return "The id must be specified like: 'comment unupvote --commentId {id_value}'";
        } else {
            try {
                CommentUpVotesService.deleteCommentUpVote(commentId);
                return String.format("You unupvoted a comment with commentId %s", commentId);
            } catch (Exception e) {
                return "Error unupvoting comment: " + e.getMessage();
            }
        }
    }

    private static TableModelBuilder<String> buildCommentTable(JsonNode commentNode) {
        TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
        modelBuilder.addRow().addValue("ID").addValue("Content")
                .addValue("User").addValue("Email")
                .addValue("Created At").addValue("Upvotes").addValue("ThreadId");
        if (commentNode.isArray()) {
            for (JsonNode comment : commentNode) {
                JsonNode userNode = comment.path("user");
                modelBuilder.addRow()
                        .addValue(comment.path("id").asText("N/A"))
                        .addValue(comment.path("content").asText("N/A"))
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
                    .addValue(userNode.path("username").asText("N/A"))
                    .addValue(userNode.path("email").asText("N/A"))
                    .addValue(commentNode.path("createdAt").asText("N/A"))
                    .addValue(commentNode.path("commentUpvotesCount").asText("N/A"))
                    .addValue(commentNode.path("threadId").asText("N/A"));
        }
        return modelBuilder;
    }
}
