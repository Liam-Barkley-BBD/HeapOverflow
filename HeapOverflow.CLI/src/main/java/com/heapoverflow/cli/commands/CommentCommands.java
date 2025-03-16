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
    @ShellMethod(key = "comments", value = "Get comments from the system")
    public String comments(
        @ShellOption(value = "page", help = "Page number", defaultValue = "0") int page,
        @ShellOption(value = "size", help = "Page size", defaultValue = "5") int size
    ) {
        page = page - 1;

        if(!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)){
            return "You are not logged, please login!";
        } else{
            try {

                JsonNode jsonResponse = CommentsServices.getComments(page, size);
                JsonNode contentArray = jsonResponse.path("content");

                if (!contentArray.isArray() || contentArray.isEmpty()) {
                    return "No comments found.";
                } else {

                    int totalThreads = jsonResponse.path("totalElements").asInt(0);
                    int totalPages = jsonResponse.path("totalPages").asInt(1);
                    int currentPage = jsonResponse.path("number").asInt(0) + 1;
                    boolean isLastPage = jsonResponse.path("last").asBoolean();

                    TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
                    modelBuilder.addRow().addValue("ID").addValue("Content")
                        .addValue("GID").addValue("User").addValue("Email")
                        .addValue("Created At").addValue("Upvotes").addValue("ThreadId");

                    for (JsonNode comment : contentArray) {
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

                    return TextUtils.renderTable(modelBuilder.build()) + 
                            String.format("\nPage %d of %d | Total Comments: %d %s",
                            currentPage, totalPages, totalThreads, isLastPage ? "(Last Page)" : "");

                }
            } catch (Exception e) {
                return "Error retrieving comments: " + e.getMessage();
            }
        }
    }

    @ShellMethod(key = "comment", value = "Get a specific comment in the system")
    public String comment(
        @ShellOption(value = "id", help = "the id you wish to find out more details about", defaultValue = "") String id
    ) {
        if(!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)){
            return "You are not logged, please login!";
        } else if(id.equals("")){
            return "the id must be specified like: \"comment --id {id_value}\"";
        } else{
            try{

                JsonNode comment = CommentsServices.getCommentById(id);

                TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
                    modelBuilder.addRow().addValue("ID").addValue("Content")
                        .addValue("GID").addValue("User").addValue("Email")
                        .addValue("Created At").addValue("Upvotes").addValue("ThreadId");

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

                return TextUtils.renderTable(modelBuilder.build());
            }catch(Exception error){
                return error.getMessage();
            }
        }
    }

    @ShellMethod(key = "post-comment", value = "Post a new comment")
    public String postReply(
        @ShellOption(value = "content", help = "The content you wish to add in your reply", defaultValue = "") String content,
        @ShellOption(value = "threadId", help = "The id of the thread you wish to comment on", defaultValue = "") String threadId
    ) {
        if(!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)){
            return "You are not logged, please login!";
        } else if(threadId.equals("")){
            return "the threadId must be specified like: \"post-comment --content \"{content_value}\" --threadId {threadId_value}\"";
        } else{
            try{

                JsonNode yourComment = CommentsServices.postComment(content, threadId);

                TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
                    modelBuilder.addRow().addValue("ID").addValue("Content")
                        .addValue("GID").addValue("User").addValue("Email")
                        .addValue("Created At").addValue("Upvotes").addValue("ThreadId");

                JsonNode userNode = yourComment.path("user");

                modelBuilder.addRow()
                        .addValue(yourComment.path("id").asText("N/A"))
                        .addValue(yourComment.path("content").asText("N/A"))
                        .addValue(userNode.path("id").asText("N/A"))
                        .addValue(userNode.path("username").asText("N/A"))
                        .addValue(userNode.path("email").asText("N/A"))
                        .addValue(yourComment.path("createdAt").asText("N/A"))
                        .addValue(yourComment.path("commentUpvotesCount").asText("N/A"))
                        .addValue(yourComment.path("threadId").asText("N/A"));

                return TextUtils.renderTable(modelBuilder.build());
            }catch(Exception error){
                return error.getMessage();
            }
        }
    }

    @ShellMethod(key = "edit-comment", value = "Change the content of a comment to something else")
    public String editComment(
        @ShellOption(value = "id", help = "The id of the comment you wish to edit", defaultValue = "") String id,
        @ShellOption(value = "content", help = "The content you wish to change to in your comment", defaultValue = "") String content
    ) {
        if(!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)){
            return "You are not logged, please login!";
        } else if(id.equals("")){
            return "the id must be specified like: \"edit-comment --id {id_value} --content \"{content_value}\"\"";
        } else{
            try{

                JsonNode yourComment = CommentsServices.patchComment(content, id);

                TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
                    modelBuilder.addRow().addValue("ID").addValue("Content")
                        .addValue("GID").addValue("User").addValue("Email")
                        .addValue("Created At").addValue("Upvotes").addValue("ThreadId");

                JsonNode userNode = yourComment.path("user");

                modelBuilder.addRow()
                        .addValue(yourComment.path("id").asText("N/A"))
                        .addValue(yourComment.path("content").asText("N/A"))
                        .addValue(userNode.path("id").asText("N/A"))
                        .addValue(userNode.path("username").asText("N/A"))
                        .addValue(userNode.path("email").asText("N/A"))
                        .addValue(yourComment.path("createdAt").asText("N/A"))
                        .addValue(yourComment.path("commentUpvotesCount").asText("N/A"))
                        .addValue(yourComment.path("threadId").asText("N/A"));

                return TextUtils.renderTable(modelBuilder.build());
            }catch(Exception error){
                return error.getMessage();
            }
        }
    }

    @ShellMethod(key = "delete-comment", value = "Delete your comment")
    public String deleteComment(
        @ShellOption(value = "id", help = "The id of the comment you wish to delete", defaultValue = "") String id
    ) {
        if(!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)){
            return "You are not logged, please login!";
        } else if(id.equals("")){
            return "the id must be specified like: \"delete-comment --id {id_value}\"";
        } else{
            try{

                CommentsServices.deleteComment(id);
                return String.format("Your comment to id %s has been deleted", id); 
            }catch(Exception error){
                return error.getMessage();
            }
        }
    }
}
