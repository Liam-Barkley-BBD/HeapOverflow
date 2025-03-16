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
    @ShellMethod(key = "replies", value = "Get replies from the system")
    public String replies(
        @ShellOption(value = "page", help = "Page number", defaultValue = "0") int page,
        @ShellOption(value = "size", help = "Page size", defaultValue = "5") int size
    ) {
        page = page - 1;

        if(!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)){
            return "You are not logged, please login!";
        } else{
            try {

                JsonNode jsonResponse = ReplyServices.getReplies(page, size);
                JsonNode contentArray = jsonResponse.path("content");

                if (!contentArray.isArray() || contentArray.isEmpty()) {
                    return "No replies found.";
                } else {

                    int totalThreads = jsonResponse.path("totalElements").asInt(0);
                    int totalPages = jsonResponse.path("totalPages").asInt(1);
                    int currentPage = jsonResponse.path("number").asInt(0) + 1;
                    boolean isLastPage = jsonResponse.path("last").asBoolean();

                    TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
                    modelBuilder.addRow().addValue("ID").addValue("Content").addValue("Created At")
                            .addValue("User").addValue("Email");

                    for (JsonNode reply : contentArray) {
                        JsonNode userNode = reply.path("user");

                        modelBuilder.addRow()
                                .addValue(reply.path("id").asText("N/A"))
                                .addValue(reply.path("content").asText("N/A"))
                                .addValue(reply.path("createdAt").asText("N/A"))
                                .addValue(userNode.path("username").asText("N/A"))
                                .addValue(userNode.path("email").asText("N/A"));
                    }

                    return TextUtils.renderTable(modelBuilder.build()) + 
                            String.format("\nPage %d of %d | Total Replies: %d %s",
                            currentPage, totalPages, totalThreads, isLastPage ? "(Last Page)" : "");

                }
            } catch (Exception e) {
                return "Error retrieving threads: " + e.getMessage();
            }
        }
    }

    @ShellMethod(key = "reply", value = "Get a specific reply in the system")
    public String reply(
        @ShellOption(value = "id", help = "the id you wish to find out more details about", defaultValue = "") String id
    ) {
        if(!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)){
            return "You are not logged, please login!";
        } else if(id.equals("")){
            return "the id must be specified like: \"reply --id {id_value}\"";
        } else{
            try{

                JsonNode reply = ReplyServices.getReplyById(id);

                TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
                modelBuilder.addRow().addValue("ID").addValue("Content").addValue("Created At")
                            .addValue("User").addValue("Email");

                JsonNode userNode = reply.path("user");

                modelBuilder.addRow()
                        .addValue(reply.path("id").asText("N/A"))
                        .addValue(reply.path("content").asText("N/A"))
                        .addValue(reply.path("createdAt").asText("N/A"))
                        .addValue(userNode.path("username").asText("N/A"))
                        .addValue(userNode.path("email").asText("N/A"));

                return TextUtils.renderTable(modelBuilder.build());
            }catch(Exception error){
                return error.getMessage();
            }
        }
    }

    @ShellMethod(key = "post-reply", value = "Post a new reply to a comment")
    public String postReply(
        @ShellOption(value = "content", help = "The content you wish to add in your reply", defaultValue = "") String content,
        @ShellOption(value = "commentId", help = "The id of the comment you wish to comment on", defaultValue = "") String commentId
    ) {
        if(!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)){
            return "You are not logged, please login!";
        } else if(commentId.equals("")){
            return "the commentId must be specified like: \"post-reply --content \"{content_value}\" --commentId {commentId_value}\"";
        } else{
            try{

                JsonNode yourReply = ReplyServices.postReply(content, commentId);

                TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
                modelBuilder.addRow().addValue("ID").addValue("Content").addValue("Created At")
                            .addValue("User").addValue("Email");

                JsonNode userNode = yourReply.path("user");

                modelBuilder.addRow()
                        .addValue(yourReply.path("id").asText("N/A"))
                        .addValue(yourReply.path("content").asText("N/A"))
                        .addValue(yourReply.path("createdAt").asText("N/A"))
                        .addValue(userNode.path("username").asText("N/A"))
                        .addValue(userNode.path("email").asText("N/A"));

                return TextUtils.renderTable(modelBuilder.build());
            }catch(Exception error){
                return error.getMessage();
            }
        }
    }

    @ShellMethod(key = "edit-reply", value = "Change the content of reply to something else")
    public String editReply(
        @ShellOption(value = "id", help = "The id of the reply you wish to edit", defaultValue = "") String id,
        @ShellOption(value = "content", help = "The content you wish to change to in your reply", defaultValue = "") String content
    ) {
        if(!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)){
            return "You are not logged, please login!";
        } else{
            try{

                JsonNode yourReply = ReplyServices.patchReply(content, id);

                TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
                modelBuilder.addRow().addValue("ID").addValue("Content").addValue("Created At")
                            .addValue("User").addValue("Email");

                JsonNode userNode = yourReply.path("user");

                modelBuilder.addRow()
                        .addValue(yourReply.path("id").asText("N/A"))
                        .addValue(yourReply.path("content").asText("N/A"))
                        .addValue(yourReply.path("createdAt").asText("N/A"))
                        .addValue(userNode.path("username").asText("N/A"))
                        .addValue(userNode.path("email").asText("N/A"));

                return TextUtils.renderTable(modelBuilder.build());
            }catch(Exception error){
                return error.getMessage();
            }
        }
    }

    @ShellMethod(key = "delete-reply", value = "Delete your reply")
    public String deleteReply(
        @ShellOption(value = "id", help = "The id of the reply you wish to delete", defaultValue = "") String id
    ) {
        if(!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)){
            return "You are not logged, please login!";
        } else if(id.equals("")){
            return "the commentId must be specified like: \"delete-reply --id {id_value}\"";
        } else{
            try{

                ReplyServices.deleteReply(id);
                return String.format("Your reply to %s has been deleted", id); 
            }catch(Exception error){
                return error.getMessage();
            }
        }
    }
}
