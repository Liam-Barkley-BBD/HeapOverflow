package com.heapoverflow.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heapoverflow.cli.services.ThreadsService;
import com.heapoverflow.cli.utils.EnvUtils;

@ShellComponent
public class ThreadCommands {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ShellMethod(key = "threads", value = "Get threads")
    public String getThreads() {
        if (EnvUtils.retrieveJwt().isEmpty()) {
            return "You are not logged in, please login!";
        }

        try {
            String jsonResponse = ThreadsService.getThreads().toString();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode contentArray = rootNode.path("content");

            if (!contentArray.isArray() || contentArray.isEmpty()) {
                return "No threads found.";
            }

            TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
            modelBuilder.addRow().addValue("ID").addValue("Title").addValue("Created At").addValue("Closed At");

            for (JsonNode thread : contentArray) {
                modelBuilder.addRow()
                        .addValue(thread.path("id").asText("N/A"))
                        .addValue(thread.path("title").asText("N/A"))
                        .addValue(thread.path("createdAt").asText("N/A"))
                        .addValue(thread.path("closedAt").isNull() ? "Open" : thread.path("closedAt").asText());
            }

            return renderTable(modelBuilder.build());

        } catch (Exception e) {
            return "Error retrieving threads: " + e.getMessage();
        }
    }

    @ShellMethod(key = "thread", value = "Get a thread by ID")
    public String getThreadById(@ShellOption(help = "Thread ID") int id) {
        if (EnvUtils.retrieveJwt().isEmpty()) {
            return "You are not logged in, please login!";
        }

        try {
            String jsonResponse = ThreadsService.getThreadsById(id).toString();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            if (rootNode == null || !rootNode.isObject()) {
                return "Thread not found.";
            }

            TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
            modelBuilder.addRow().addValue("ID")
                    .addValue("Title")
                    .addValue("Description")
                    .addValue("Created At")
                    .addValue("Closed At")
                    .addValue("User ID")
                    .addValue("Upvotes");

            modelBuilder.addRow()
                    .addValue(rootNode.path("id").asText("N/A"))
                    .addValue(rootNode.path("title").asText("N/A"))
                    .addValue(rootNode.path("description").asText("N/A"))
                    .addValue(rootNode.path("createdAt").asText("N/A"))
                    .addValue(rootNode.path("closedAt").isNull() ? "Open" : rootNode.path("closedAt").asText())
                    .addValue(rootNode.path("userId").asText("N/A"))
                    .addValue(rootNode.path("threadUpvotesCount").asText("0"));

            return renderTable(modelBuilder.build());

        } catch (Exception e) {
            return "Error retrieving thread: " + e.getMessage();
        }
    }

    // @ShellMethod(key = "post-thread", value = "Create a new thread")
    // public String postThread(
    // @ShellOption(help = "UserId") String UserId,
    // @ShellOption(help = "Thread Title") String title,
    // @ShellOption(help = "Thread Description") String description) {

    // if (EnvUtils.retrieveJwt().isEmpty()) {
    // return "You are not logged in, please login!";
    // }

    // try {
    // String jsonResponse = ThreadsService.postThread(title, description);
    // JsonNode rootNode = objectMapper.readTree(jsonResponse);

    // if (rootNode == null || !rootNode.isObject()) {
    // return "Failed to create thread.";
    // }

    // TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
    // modelBuilder.addRow().addValue("ID").addValue("Title").addValue("Description").addValue("Created
    // At");

    // modelBuilder.addRow()
    // .addValue(rootNode.path("id").asText("N/A"))
    // .addValue(rootNode.path("title").asText("N/A"))
    // .addValue(rootNode.path("description").asText("N/A"))
    // .addValue(rootNode.path("createdAt").asText("N/A"));

    // return renderTable(modelBuilder.build());

    // } catch (Exception e) {
    // return "Error creating thread: " + e.getMessage();
    // }
    // }

    private String renderTable(TableModel model) {
        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addFullBorder(BorderStyle.oldschool);
        return tableBuilder.build().render(120);
    }
}
