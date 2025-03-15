package com.heapoverflow.cli.commands;

import java.util.List;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heapoverflow.cli.entities.User;
import com.heapoverflow.cli.services.UserServices;
import com.heapoverflow.cli.utils.EnvUtils;


@ShellComponent
public class UserCommands {
    @ShellMethod(key = "getUsers", value = "Get users")
    public String getUsers(
        @ShellOption(value = "username", defaultValue = "") String username, 
        @ShellOption(value = "email", defaultValue = "") String email
    ) {
        if(EnvUtils.retrieveJwt().equals("")){
            return "You are not logged, please login!";
        } else{
            try{
                JsonNode result = UserServices.getUsers(username, email);
                if(!result.has("content")){
                    return "no response was returned from the api";
                } else{
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<User> users = objectMapper.readValue(result.get("content").toString(), new TypeReference<List<User>>() {});
                    StringBuilder output = new StringBuilder();
    
                    for (User user : users) {
                        AttributedString styledRow = new AttributedString(
                            String.format("%-15s | %-15s | %-15s", user.getId(), user.getUsername(), user.getEmail()),
                            AttributedStyle.DEFAULT.bold()
                        );
                        output.append(styledRow.toAnsi() + "\n");
                    }
    
                    return output.toString();
                }
            } catch(Exception error){
                return error.getMessage();
            }
        }
    }

    @ShellMethod(key = "getUser", value = "Get user")
    public String getUser(
        @ShellOption(value = "gid", defaultValue = "") String gid
    ) {
        if(EnvUtils.retrieveJwt().equals("")){
            return "You are not logged, please login!";
        } else{
            return "";//UserServices.getUsersByGoogleId(gid).toString();
        }
    }
}
