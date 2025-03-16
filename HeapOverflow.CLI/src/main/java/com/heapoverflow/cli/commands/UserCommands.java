package com.heapoverflow.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.TableModelBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.UserServices;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.TextUtils;

@ShellComponent
public class UserCommands {
    @ShellMethod(key = "users", value = "Get all users in the system")
    public String getUsers(
        @ShellOption(value = "username", help = "The username you wish to match for", defaultValue = "") String username, 
        @ShellOption(value = "email", help = "The email you wish to match with", defaultValue = "") String email,
        @ShellOption(value = "page", help = "Page number", defaultValue = "0") int page,
        @ShellOption(value = "help", help = "Page size", defaultValue = "5") int size
    ) {
        page = page - 1;

        if(!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)){
            return "You are not logged, please login!";
        } else{
            // attempt to get users
        }

        try{
            JsonNode result = UserServices.getUsers(username, email, page, size);
            JsonNode contentArray = result.path("content");

            if (!contentArray.isArray() || contentArray.isEmpty()) {
                return "No users found.";
            } else{
                // there are users and we should print them out
            }

            int totalThreads = result.path("totalElements").asInt(0);
            int totalPages = result.path("totalPages").asInt(1);
            int currentPage = result.path("number").asInt(0) + 1;
            boolean isLastPage = result.path("last").asBoolean();

            TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
            modelBuilder.addRow().addValue("id").addValue("username").addValue("email");

            for (JsonNode user : contentArray) {
                modelBuilder.addRow()
                        .addValue(user.path("id").asText("N/A"))
                        .addValue(user.path("username").asText("N/A"))
                        .addValue(user.path("email").asText("N/A"));
            }

            return TextUtils.renderTable(modelBuilder.build()) + 
                            String.format("\nPage %d of %d | Total Replies: %d %s",
                            currentPage, totalPages, totalThreads, isLastPage ? "(Last Page)" : "");
        } catch(Exception error){
            return error.getMessage();
        }
    }

    @ShellMethod(key = "user", value = "Get a specific user in the system")
    public String getUser(
        @ShellOption(value = "gid", help = "the google user id you wish to find out more details about", defaultValue = "") String gid
    ) {
        if(!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)){
            return "You are not logged, please login!";
        } else if(gid.equals("")){
            return "the gid must be specified like: \"user --gid {gid_value}\"";
        } else{
            try{

                JsonNode user = UserServices.getUsersByGoogleId(gid);

                TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
                modelBuilder.addRow().addValue("id").addValue("username").addValue("email");

                modelBuilder.addRow()
                        .addValue(user.path("id").asText("N/A"))
                        .addValue(user.path("username").asText("N/A"))
                        .addValue(user.path("email").asText("N/A"));

                return TextUtils.renderTable(modelBuilder.build());
            }catch(Exception error){
                return error.getMessage();
            }
        }
    }
}
