package com.heapoverflow.cli.commands;

import java.util.List;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.AuthServices;
import com.heapoverflow.cli.utils.EnvUtils;
import com.heapoverflow.cli.utils.FlagsCheckUtils;

@ShellComponent
public class AuthCommand {

    @ShellMethod(key = "auth", value = "Authentication commands: login, logout, gid, name")
    public String auth(
        @ShellOption(value = "login", help = "Attempt to login", defaultValue = "false") boolean login,
        @ShellOption(value = "logout", help = "Attempt to logout", defaultValue = "false") boolean logout,
        @ShellOption(value = "gid", defaultValue = "false") boolean gid,
        @ShellOption(value = "name", help = "See your Google name", defaultValue = "false") boolean name,
        @ShellOption(value = "jwt", defaultValue = "false") boolean jwt
    ) {
        List<String> selectedFlags = FlagsCheckUtils.ensureOnlyOneFlagIsSetForAuth(login, logout, gid, name, jwt);
        if(selectedFlags.size() > 1){
            return "You cannot use multiple action based flags at once: " + selectedFlags.toString();
        } else if (login) {
            return handleLogin();
        } else if (logout) {
            return handleLogout();
        } else if (gid) {
            return handleGid();
        } else if (name) {
            return handleName();
        } else if(jwt){
            return handleJwt();
        } else {
            return "Invalid command. Use: \n" +
                                        "\t\t\t--login\n" +
                                        "\t\t\t--logout\n" +
                                        "\t\t\t--name\n" +
                                        "\t\t\t--help\n";
        }
    }

    private String handleLogin() {
        if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return AuthServices.attemptGoogleLogin();
        } else {
            return "You are already logged in";
        }
    }

    private String handleLogout() {
        try {
            if (!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
                return "You are not logged in";
            } else {
                EnvUtils.deleteKeys();
                return "You have been logged out";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String handleGid() {
        if (EnvUtils.doesKeyExist(EnvConstants.GOOGLE_SUB)) {
            return EnvUtils.retrieveValue(EnvConstants.GOOGLE_SUB);
        } else {
            return "You are not logged in or your sub is not set, logout and attempt to login again";
        }
    }

    private String handleName() {
        if (EnvUtils.doesKeyExist(EnvConstants.GOOGLE_NAME)) {
            return EnvUtils.retrieveValue(EnvConstants.GOOGLE_NAME);
        } else {
            return "You are not logged in or your name is not set, logout and attempt to login again";
        }
    }

    private String handleJwt() {
        if (EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)) {
            return EnvUtils.retrieveValue(EnvConstants.JWT_TOKEN);
        } else {
            return "You are not logged in or your jwt is not set, logout and attempt to login again";
        }
    }
}