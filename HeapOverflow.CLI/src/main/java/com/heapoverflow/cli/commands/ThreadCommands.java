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
import com.heapoverflow.cli.utils.FlagsCheckUtils;
import com.heapoverflow.cli.utils.TextUtils;
import java.util.List;
import java.util.Optional;

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
            @ShellOption(value = "closeThread", help = "Close Thread (true/false)", defaultValue = "false") boolean closeThread,
            @ShellOption(value = "search", help = "Search query", defaultValue = "") Optional<String> search,
            @ShellOption(value = "threadId", help = "Thread ID", defaultValue = ShellOption.NULL) Optional<String> threadId,
            @ShellOption(value = "title", help = "Thread title", defaultValue = ShellOption.NULL) Optional<String> title,
            @ShellOption(value = "description", help = "Thread description", defaultValue = ShellOption.NULL) Optional<String> description,
            @ShellOption(value = "page", help = "Page number", defaultValue = "1") Integer page,
            @ShellOption(value = "size", help = "Page size", defaultValue = "10") Integer size) {

        List<String> selectedFlags = FlagsCheckUtils.ensureOnlyOneFlagIsSetForComments(list, get, post, edit, delete,
                upvote, removeUpvote);
        if (selectedFlags.size() > 1) {
            return "You cannot use multiple action based flags at once: " + selectedFlags.toString();
        } else if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return "You are not logged in. Please log in!";
        } else if (list) {
            return getAllThreads(search.orElse(""), page, size);
        } else if (trending) {
            return getTrendingThreads(page, size);
        } else if (userThreads) {
            return getUserThreads(page, size);
        } else if (get) {
            return getThread(threadId.orElse("")) + "Comments for thread: \n"
                    + CommentCommands.getAllComments(page, size, threadId.orElse(""));
        } else if (post) {
            return postThread(title.orElse(""), description.orElse(""));
        } else if (edit) {
            return editThread(threadId.orElse(""), title.orElse(""), description.orElse(""), closeThread);
        } else if (delete) {
            return deleteThread(threadId.orElse(""));
        } else if (upvote) {
            return upvoteThread(threadId.orElse(""));
        } else if (removeUpvote) {
            return removeUpvoteThread(threadId.orElse(""));
        } else {
            return "Invalid command. Use: \n" +
                    "\t--list [--search[optional] \"query\" --page[optional] {num} --size[optional] {num}]\n" +
                    "\t--userThreads [ --page[optional] {num} --size[optional] {num}]\n" +
                    "\t--trending [ --page[optional] {num} --size[optional] {num}]\n" +
                    "\t--get --threadId {id} --page[optional] {num} --size[optional] {num}]\n" +
                    "\t--post --title \"text\" --description \"text\"\n" +
                    "\t--edit --threadId {id} --title \"text\" --description \"text\" [--closeThread true/false]\n" +
                    "\t--delete --threadId {id}\n" +
                    "\t--upvote --threadId {id}\n" +
                    "\t--removeUpvote --threadId {id}\n" +
                    "\t--help";
        }
    }

    private String getAllThreads(String search, Integer page, Integer size) {
        if (page == null) {
            return "page has to be specified eg: thread --list --page 2";
        } else if (size == null) {
            return "size has to be specified eg: thread --list --size 2";
        } else {

            try {

                JsonNode jsonResponse = ThreadsService.getThreads(search, Math.max(0, page - 1), size);
                JsonNode contentArray = jsonResponse.path("content");

                {
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
    }

    private String getThread(String threaId) {
        if (threaId == null) {
            return "Thread ID must be specified like: 'thread get --threadId {id_value}'";
        } else {
            try {
                JsonNode thread = ThreadsService.getThreadsById(threaId);
                String threadTable = buildThreadTable(thread, true);
                return threadTable;
            } catch (Exception e) {
                return "Error retrieving thread: " + e.getMessage();
            }
        }
    }

    private String getUserThreads(Integer page, Integer size) {
        if (page == null) {
            return "page has to be specified eg: thread --userThreads --page 2";
        } else if (size == null) {
            return "size has to be specified eg: thread --userThreads --size 2";
        } else {

            try {
                JsonNode jsonResponse = ThreadsService.getThreadsByUser(Math.max(0, page - 1), size);
                JsonNode contentArray = jsonResponse.path("content");

                {
                    int totalThreads = jsonResponse.path("totalElements").asInt(0);
                    int totalPages = jsonResponse.path("totalPages").asInt(1);
                    int currentPage = jsonResponse.path("number").asInt(0) + 1;
                    boolean isLastPage = jsonResponse.path("last").asBoolean();

                    return buildThreadTable(contentArray, false) +
                            String.format("\nPage %d of %d | Total Threads: %d %s",
                                    currentPage, totalPages, totalThreads, isLastPage ? "(Last Page)" : "");

                }
            } catch (Exception e) {
                return "Error retrieving thread: " + e.getMessage();
            }
        }
    }

    private String getTrendingThreads(Integer page, Integer size) {
        if (page == null) {
            return "page has to be specified eg: thread --trending --page 2";
        } else if (size == null) {
            return "size has to be specified eg: thread --trending --size 2";
        } else {

            try {
                JsonNode jsonResponse = ThreadsService.getThreadsTrending(Math.max(0, page - 1), size);
                JsonNode contentArray = jsonResponse.path("content");

                {
                    int totalThreads = jsonResponse.path("totalElements").asInt(0);
                    int totalPages = jsonResponse.path("totalPages").asInt(1);
                    int currentPage = jsonResponse.path("number").asInt(0) + 1;
                    boolean isLastPage = jsonResponse.path("last").asBoolean();

                    return buildThreadTable(contentArray, false) +
                            String.format("\nPage %d of %d | Total Threads: %d %s",
                                    currentPage, totalPages, totalThreads, isLastPage ? "(Last Page)" : "");

                }
            } catch (Exception e) {
                return "Error retrieving thread: " + e.getMessage();
            }
        }
    }

    private String postThread(String title, String description) {
        if (title == null || description == null) {
            return "title and description must be specified like:'thread --post --title\"{title _value}\" --description \"{description_value}\"' ";
        } else {
            try {
                JsonNode thread = ThreadsService.postThread(title, description);
                return "Thread created successfully: " + thread.path("id").asText() + "\n" +
                        buildThreadTable(thread, true);
            } catch (Exception e) {
                return "Error creating thread: " + e.getMessage();
            }
        }
    }

    private String editThread(String threadId, String title, String description, Boolean closeThread) {
        if (threadId == null) {
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
        if (threadId == null) {
            return "The threadId must be specified like: 'thread --delete --threadId {threadId_value}'";
        } else {
            try {
                ThreadsService.deleteThread(threadId);
                return "Thread deleted successfully.";
            } catch (Exception e) {
                return "Error deleting thread: " + e.getMessage();
            }
        }
    }

    private String upvoteThread(String threadId) {
        if (threadId == null) {
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
        if (threadId == null) {
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
