package com.heapoverflow.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.TableModelBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.heapoverflow.cli.services.UserServices;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.TextUtils;


@ShellComponent
public class UserCommands {
    @ShellMethod(key = "users", value = "Get all users in the system")
    public String getUsers(
        @ShellOption(value = "username", defaultValue = "") String username, 
        @ShellOption(value = "email", defaultValue = "") String email
    ) {
        if(!EnvUtils.doesJwtExist()){
            return "You are not logged, please login!";
        } else{
            // attempt to get users
        }

        try{
            JsonNode result = UserServices.getUsers(username, email);
            JsonNode contentArray = result.path("content");

            if (!contentArray.isArray() || contentArray.isEmpty()) {
                return "No users found.";
            } else{
                // there are users and we should print them out
            }

            TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
            modelBuilder.addRow().addValue("id").addValue("username").addValue("email");

            for (JsonNode thread : contentArray) {
                modelBuilder.addRow()
                        .addValue(thread.path("id").asText("N/A"))
                        .addValue(thread.path("username").asText("N/A"))
                        .addValue(thread.path("email").asText("N/A"));
            }

            return TextUtils.renderTable(modelBuilder.build());
        } catch(Exception error){
            return error.getMessage();
        }
    }

    @ShellMethod(key = "user", value = "Get a specific user in the system")
    public String getUser(
        @ShellOption(value = "gid", defaultValue = "") String gid
    ) {
        if(!EnvUtils.doesJwtExist()){
            return "You are not logged, please login!";
        } else{
            try{
                return UserServices.getUsersByGoogleId(gid).toString();
            }catch(Exception error){
                return error.getMessage();
            }
        }
    }
}
