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
}
