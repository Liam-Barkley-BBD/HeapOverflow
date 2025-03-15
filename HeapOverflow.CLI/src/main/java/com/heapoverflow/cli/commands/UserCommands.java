package com.heapoverflow.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

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
            return "";
            /*try{
                JsonNode result = UserServices.getUsers(username, email);
                ObjectMapper objectMapper = new ObjectMapper();
                List<User> users = objectMapper.readValue(result.getValue("content").toString(), new TypeReference<List<User>>() {});
                StringBuilder output = new StringBuilder();

                for (User user : users) {
                    AttributedString styledRow = new AttributedString(
                        String.format("%-15s | %-15s | %-15s", user.getId(), user.getUsername(), user.getEmail()),
                        AttributedStyle.DEFAULT.bold()
                    );
                    output.append(styledRow.toAnsi() + "\n");
                }

                return output.toString();

                ObjectMapper objectMapper = new ObjectMapper();
                Object content = result.get("content"); // No .getString()!

                List<User> users = switch(result.get("content")){
                    case  List -> objectMapper.convertValue(content, new TypeReference<List<User>>() {});
                    default -> new List<>();
                }; 


            } catch(Exception error){
                return error.getMessage();
            }*/
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
