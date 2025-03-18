package com.heapoverflow.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.TableModelBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.ThreadsService;
import com.heapoverflow.cli.services.ThreadUpvoteServices;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.TextUtils;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@ShellComponent
public class ThreadCommands {

    @ShellMethod(key = "thread", value = "Manage threads in the system")
    public String thread(
            @ShellOption(value = "list", help = "List threads", defaultValue = "false") boolean list,
            @ShellOption(value = "trending", help = "List threads by trending", defaultValue = "false") boolean trending,
            @ShellOption(value = "userThreads", help = "List threads by user", defaultValue = "false") boolean userThreads,
            @ShellOption(value = "get", help = "Get a specific thread", defaultValue = "false") boolean get,
            @ShellOption(value = "post", help = "Post a new thread", defaultValue = "false") boolean post,
            @ShellOption(value = "edit", help = "Edit a thread", defaultValue = "false") boolean edit,
            @ShellOption(value = "delete", help = "Delete a thread", defaultValue = "false") boolean delete,
            @ShellOption(value = "upvote", help = "Upvote a thread", defaultValue = "false") boolean upvote,
            @ShellOption(value = "removeUpvote", help = "Remove upvote from a thread", defaultValue = "false") boolean removeUpvote,
            @ShellOption(value = "search", help = "Search query", defaultValue = "") String search,
            @ShellOption(value = "closeThread", help = "Close Thread (true/false)", defaultValue = "false") boolean closeThread,
            @ShellOption(value = "threadId", help = "Thread ID", defaultValue = "") String threadId,
            @ShellOption(value = "title", help = "Thread title", defaultValue = "") String title,
            @ShellOption(value = "description", help = "Thread description", defaultValue = "") String description,
            @ShellOption(value = "page", help = "Page number", defaultValue = "1") int page,
            @ShellOption(value = "size", help = "Page size", defaultValue = "10") int size) {
        if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return "You are not logged in. Please log in!";
        } else if (list) {
            return getAllThreads(search, page, size);
        } else if (trending) {
            return getTrendingThreads();
        } else if (userThreads) {
            return getUserThreads();
        } else if (get) {
            return getThread(threadId) + CommentCommands.getAllComments(page, size, threadId);
        } else if (post) {
            return postThread(title, description);
        } else if (edit) {
            return editThread(threadId, title, description, closeThread);
        } else if (delete) {
            return deleteThread(threadId);
        } else if (upvote) {
            return upvoteThread(threadId);
        } else if (removeUpvote) {
            return removeUpvoteThread(threadId);
        } else {
            return "Invalid command. Use: \n" +
                    "\t--list [--search[optional] \"query\" --page[optional] {num} --size[optional] {num}]\n" +
                    "\t--userThreads \n" +
                    "\t--trending \n" +
                    "\t--get --threadId {id} --page[optional] {num} --size[optional] {num}]\n" +
                    "\t--post --title \"text\" --description \"text\"\n" +
                    "\t--edit --threadId {id} --title \"text\" --description \"text\" [--closeThread true/false]\n" +
                    "\t--delete --threadId {id}\n" +
                    "\t--upvote --threadId {id}\n" +
                    "\t--removeUpvote --threadId {id}\n" +
                    "\t--help";
        }
    }

    private String getAllThreads(String search, int page, int size) {
        try {
            String encodedSearchText = URLEncoder.encode(search, StandardCharsets.UTF_8.toString());
            JsonNode jsonResponse = ThreadsService.getThreads(encodedSearchText, Math.max(0, page - 1), size);
            JsonNode contentArray = jsonResponse.path("content");

            if (!contentArray.isArray() || contentArray.isEmpty()) {
                return "No threads found.";
            } else {
                int totalThreads = jsonResponse.path("totalElements").asInt(0);
                int totalPages = jsonResponse.path("totalPages").asInt(1);
                int currentPage = jsonResponse.path("number").asInt(0) + 1;
                boolean isLastPage = jsonResponse.path("last").asBoolean();

                return buildThreadTable(contentArray, false) +
                        String.format("\nPage %d of %d | Total Threads: %d %s",
                                currentPage, totalPages, totalThreads, isLastPage ? "(Last Page)" : "");
            }
        } catch (Exception e) {
            return "Error retrieving threads: " + e.getMessage();
        }
    }

    private String getThread(String id) {
        if (id.isEmpty()) {
            return "Thread ID must be specified.";
        } else {
            try {
                JsonNode thread = ThreadsService.getThreadsById(id);
                String threadTable = buildThreadTable(thread, true);
                return threadTable;
            } catch (Exception e) {
                return "Error retrieving thread: " + e.getMessage();
            }
        }
    }

    private String getUserThreads() {

        {
            try {
                JsonNode jsonResponse = ThreadsService.getThreadsByUser();
                JsonNode contentArray = jsonResponse.path("content");

                if (!contentArray.isArray() || contentArray.isEmpty()) {
                    return "No comments found.";
                } else {
                    if (!contentArray.isArray() || contentArray.isEmpty()) {
                        return "No threads found.";
                    } else {
                        int totalThreads = jsonResponse.path("totalElements").asInt(0);
                        int totalPages = jsonResponse.path("totalPages").asInt(1);
                        int currentPage = jsonResponse.path("number").asInt(0) + 1;
                        boolean isLastPage = jsonResponse.path("last").asBoolean();

                        return buildThreadTable(contentArray, false) +
                                String.format("\nPage %d of %d | Total Threads: %d %s",
                                        currentPage, totalPages, totalThreads, isLastPage ? "(Last Page)" : "");
                    }
                }
            } catch (Exception e) {
                return "Error retrieving thread: " + e.getMessage();
            }
        }
    }

    private String getTrendingThreads() {

        {
            try {
                JsonNode jsonResponse = ThreadsService.getThreadsTrending();
                JsonNode contentArray = jsonResponse.path("content");

                if (!contentArray.isArray() || contentArray.isEmpty()) {
                    return "No comments found.";
                } else {
                    if (!contentArray.isArray() || contentArray.isEmpty()) {
                        return "No threads found.";
                    } else {
                        int totalThreads = jsonResponse.path("totalElements").asInt(0);
                        int totalPages = jsonResponse.path("totalPages").asInt(1);
                        int currentPage = jsonResponse.path("number").asInt(0) + 1;
                        boolean isLastPage = jsonResponse.path("last").asBoolean();

                        return buildThreadTable(contentArray, false) +
                                String.format("\nPage %d of %d | Total Threads: %d %s",
                                        currentPage, totalPages, totalThreads, isLastPage ? "(Last Page)" : "");
                    }
                }
            } catch (Exception e) {
                return "Error retrieving thread: " + e.getMessage();
            }
        }
    }

    private String postThread(String title, String description) {
        try {
            JsonNode thread = ThreadsService.postThread(title, description);
            return "Thread created successfully: " + thread.path("id").asText() + "\n" +
                    buildThreadTable(thread, true);
        } catch (Exception e) {
            return "Error creating thread: " + e.getMessage();
        }
    }

    private String editThread(String threadId, String title, String description, Boolean closeThread) {
        if (threadId.isEmpty()) {
            return "ThreadId should be initialized eg thread --edit --threadId {id}";
        } else {
            try {
                JsonNode thread = ThreadsService.patchThread(threadId, title, description, closeThread);
                return buildThreadTable(thread, true);
            } catch (Exception e) {
                return "Error updating thread: " + e.getMessage();
            }
        }
    }

    private String deleteThread(String threadId) {
        try {
            ThreadsService.deleteThread(threadId);
            return "Thread deleted successfully.";
        } catch (Exception e) {
            return "Error deleting thread: " + e.getMessage();
        }
    }

    private String upvoteThread(String threadId) {
        if (threadId.isEmpty()) {
            return "Thread ID must be specified to upvote: 'thread upvote --threadId {id_value}";
        } else {
            try {
                ThreadUpvoteServices.postThreadUpVote(Integer.parseInt(threadId));
                return "Successfully upvoted thread ID: " + threadId;
            } catch (Exception e) {
                return "Error upvoting thread: " + e.getMessage();
            }
        }
    }

    private String removeUpvoteThread(String threadId) {
        if (threadId.isEmpty()) {
            return "Thread Id must be specified like: 'thread unupvote --threadId {id_value}'";
        } else {
            try {
                ThreadUpvoteServices.deleteThreadUpVote(threadId);
                return "Successfully removed upvote from thread ID: " + threadId;
            } catch (Exception e) {
                return "Error removing upvote: " + e.getMessage();
            }
        }
    }

    private String buildThreadTable(JsonNode threads, boolean includeDescription) {
        TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();

        if (includeDescription) {
            modelBuilder.addRow().addValue("ID").addValue("Title").addValue("Description").addValue("Created At")
                    .addValue("Closed At").addValue("Upvotes").addValue("User");
        } else {
            modelBuilder.addRow().addValue("ID").addValue("Title").addValue("Created At")
                    .addValue("Closed At").addValue("Upvotes").addValue("User");
        }

        if (threads.isArray()) {
            for (JsonNode thread : threads) {
                addThreadRow(modelBuilder, thread, includeDescription);
            }
        } else {
            addThreadRow(modelBuilder, threads, includeDescription);
        }

        return TextUtils.renderTable(modelBuilder.build());
    }

    private void addThreadRow(TableModelBuilder<String> modelBuilder, JsonNode thread, boolean includeDescription) {
        JsonNode userNode = thread.path("user");

        if (includeDescription) {
            modelBuilder.addRow()
                    .addValue(thread.path("id").asText("N/A"))
                    .addValue(thread.path("title").asText("N/A"))
                    .addValue(thread.path("description").asText("N/A"))
                    .addValue(thread.path("createdAt").asText("N/A"))
                    .addValue(thread.path("closedAt").isNull() ? "Open" : thread.path("closedAt").asText())
                    .addValue(thread.path("threadUpvotesCount").asText("0"))
                    .addValue(userNode.path("username").asText("Anonymous"));
        } else {
            modelBuilder.addRow()
                    .addValue(thread.path("id").asText("N/A"))
                    .addValue(thread.path("title").asText("N/A"))
                    .addValue(thread.path("createdAt").asText("N/A"))
                    .addValue(thread.path("closedAt").isNull() ? "Open" : thread.path("closedAt").asText())
                    .addValue(thread.path("threadUpvotesCount").asText("0"))
                    .addValue(userNode.path("username").asText("Anonymous"));
        }
    }
}
