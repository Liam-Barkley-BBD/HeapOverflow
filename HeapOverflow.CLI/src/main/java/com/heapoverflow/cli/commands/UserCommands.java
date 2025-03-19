package com.heapoverflow.cli.commands;

import java.util.List;
import java.util.Optional;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.TableModelBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.UserServices;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.FlagsCheckUtils;
import com.heapoverflow.cli.utils.TextUtils;

@ShellComponent
public class UserCommands {
    @ShellMethod(key = "users", value = "Manage users in the system")
    public String users(
        @ShellOption(value = "list", help = "List all users", defaultValue = "false") boolean list,
        @ShellOption(value = "get", help = "Get a specific user", defaultValue = "false") boolean get,
        @ShellOption(value = "gid", help = "Google user ID", defaultValue = ShellOption.NULL) Optional<String> gid,
        @ShellOption(value = "username", help = "Filter by username", defaultValue = ShellOption.NULL) Optional<String> username,
        @ShellOption(value = "email", help = "Filter by email", defaultValue = ShellOption.NULL) Optional<String> email,
        @ShellOption(value = "page", help = "Page number", defaultValue = ShellOption.NULL) Optional<Integer> page,
        @ShellOption(value = "size", help = "Page size", defaultValue = ShellOption.NULL) Optional<Integer> size
    ) {
        List<String> selectedFlags = FlagsCheckUtils.ensureOnlyOneFlagIsSetForUser(list, get);
        if(selectedFlags.size() > 1){
            return "You cannot use multiple action based flags at once: " + selectedFlags.toString();
        } else if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return "You are not logged in, please login!";
        } else if (get && gid.isPresent() && gid.isEmpty()) {
            return "You must specify a Google ID with --gid";
        } else if (get && gid.isPresent() && !gid.isEmpty()) {
            return getUserByGid(gid.orElse(""));
        } else if (list) {
            return listUsers(username.orElse(""), email.orElse(""), page.orElse(1), size.orElse(10));
        } else{
            return "Specify --list to retrieve users or --get --gid {gid} to get a specific user.";
        }
    }

    private String getUserByGid(String gid) {
        try {
            JsonNode user = UserServices.getUsersByGoogleId(gid);
            TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
            modelBuilder.addRow().addValue("id").addValue("username").addValue("email");
            modelBuilder.addRow()
                .addValue(user.path("id").asText("N/A"))
                .addValue(user.path("username").asText("N/A"))
                .addValue(user.path("email").asText("N/A"));
            return TextUtils.renderTable(modelBuilder.build());
        } catch (Exception error) {
            return error.getMessage();
        }
    }

    private String listUsers(String username, String email, int page, int size) {
        try {
            JsonNode result = UserServices.getUsers(username, email, Math.max(0, page - 1), size);
            JsonNode contentArray = result.path("content");
            if (!contentArray.isArray() || contentArray.isEmpty()) {
                return "No users found.";
            } else{
                // continue
            }

            TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
            modelBuilder.addRow().addValue("id").addValue("username").addValue("email");
            for (JsonNode user : contentArray) {
                modelBuilder.addRow()
                    .addValue(user.path("id").asText("N/A"))
                    .addValue(user.path("username").asText("N/A"))
                    .addValue(user.path("email").asText("N/A"));
            }

            int totalThreads = result.path("totalElements").asInt(0);
            int totalPages = result.path("totalPages").asInt(1);
            int currentPage = result.path("number").asInt(0) + 1;
            boolean isLastPage = result.path("last").asBoolean();

            return TextUtils.renderTable(modelBuilder.build()) + 
                String.format("\nPage %d of %d | Total Users: %d %s",
                currentPage, totalPages, totalThreads, isLastPage ? "(Last Page)" : "");
        } catch (Exception error) {
            return error.getMessage();
        }
    }
}
