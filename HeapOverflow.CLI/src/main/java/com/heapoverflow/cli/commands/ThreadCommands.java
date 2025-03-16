package com.heapoverflow.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.ThreadsService;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.TextUtils;

@ShellComponent
public class ThreadCommands {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ShellMethod(key = "threads", value = "Get threads")

    public String getThreads(
            @ShellOption(value = "title", help = "Thread Title", defaultValue = "") String title,
            @ShellOption(value = "description", help = "Thread Description", defaultValue = "") String description,
            @ShellOption(value = "page", help = "Page number", defaultValue = "0") int page,
            @ShellOption(value = "size", help = "Page size", defaultValue = "5") int size) {

        page = page - 1;

        if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return "You are not logged in, please login!";
        } else {
            try {

                String jsonResponse = ThreadsService.getThreads(title, description, page, size).toString();
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                JsonNode contentArray = rootNode.path("content");

                if (!contentArray.isArray() || contentArray.isEmpty()) {
                    return "No threads found.";
                } else {

                    int totalThreads = rootNode.path("totalElements").asInt(0);
                    int totalPages = rootNode.path("totalPages").asInt(1);
                    int currentPage = rootNode.path("number").asInt(0) + 1;
                    boolean isLastPage = rootNode.path("last").asBoolean();

                    TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
                    modelBuilder.addRow().addValue("ID").addValue("Title").addValue("Created At")
                            .addValue("Closed At").addValue("Upvotes").addValue("User");

                    for (JsonNode thread : contentArray) {
                        JsonNode userNode = thread.path("user");
                        String username = (userNode.isMissingNode() || userNode.isNull()
                                || userNode.path("username").isNull())
                                        ? "Anonymous"
                                        : userNode.path("username").asText();

                        modelBuilder.addRow()
                                .addValue(thread.path("id").asText("N/A"))
                                .addValue(thread.path("title").asText("N/A"))
                                .addValue(thread.path("createdAt").asText("N/A"))
                                .addValue(thread.path("closedAt").isNull() ? "Open" : thread.path("closedAt").asText())
                                .addValue(thread.path("threadUpvotesCount").asText("0"))
                                .addValue(username);
                    }

                    String tableOutput = TextUtils.renderTable(modelBuilder.build());

                    return tableOutput + String.format("\nPage %d of %d | Total Threads: %d %s",
                            currentPage, totalPages, totalThreads, isLastPage ? "(Last Page)" : "");

                }
            } catch (Exception e) {
                return "Error retrieving threads: " + e.getMessage();
            }
        }
    }

    @ShellMethod(key = "thread", value = "Get a thread by ID")
    public String getThreadById(@ShellOption(value = "id", help = "Thread ID") int id) {
        if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return "You are not logged in, please login!";
        } else {

            try {
                String jsonResponse = ThreadsService.getThreadsById(id).toString();
                JsonNode rootNode = objectMapper.readTree(jsonResponse);

                if (rootNode == null || !rootNode.isObject()) {
                    return "Thread not found.";
                } else {

                    String username = rootNode.path("user").path("username").asText("Unknown");

                    TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
                    modelBuilder.addRow().addValue("ID")
                            .addValue("Title")
                            .addValue("Description")
                            .addValue("Created At")
                            .addValue("Closed At")
                            .addValue("User")
                            .addValue("Upvotes");

                    modelBuilder.addRow()
                            .addValue(rootNode.path("id").asText("N/A"))
                            .addValue(rootNode.path("title").asText("N/A"))
                            .addValue(rootNode.path("description").asText("N/A"))
                            .addValue(rootNode.path("createdAt").asText("N/A"))
                            .addValue(rootNode.path("closedAt").isNull() ? "Open" : rootNode.path("closedAt").asText())
                            .addValue(username)
                            .addValue(rootNode.path("threadUpvotesCount").asText("0"));

                    return TextUtils.renderTable(modelBuilder.build());
                }
            } catch (Exception e) {
                return "Error retrieving thread: " + e.getMessage();
            }
        }
    }

    @ShellMethod(key = "post-thread", value = "Create a new thread")
    public String postThread(
            @ShellOption(value = "title", help = "Thread Title") String title,
            @ShellOption(value = "description", help = "Thread Description") String description) {

        if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return "You are not logged in, please login!";
        }

        try {

            String userId = EnvUtils.retrieveValue(EnvConstants.GOOGLE_SUB);
            if (userId == null || userId.isEmpty()) {
                return "User ID not found!";
            }
            String jsonResponse = ThreadsService.postThread(title, description, userId).toString();
            System.out.println("here is the response: " + jsonResponse);
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            if (rootNode == null || !rootNode.isObject()) {
                return "Failed to create thread.";
            }

            TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
            modelBuilder.addRow().addValue("ID").addValue("Title").addValue("Description").addValue("Created At");

            modelBuilder.addRow()
                    .addValue(rootNode.path("id").asText("N/A"))
                    .addValue(rootNode.path("title").asText("N/A"))
                    .addValue(rootNode.path("description").asText("N/A"))
                    .addValue(rootNode.path("createdAt").asText("N/A"));

            return TextUtils.renderTable(modelBuilder.build());

        } catch (Exception e) {
            return "Error creating thread: " + e.getMessage();
        }
    }
}
