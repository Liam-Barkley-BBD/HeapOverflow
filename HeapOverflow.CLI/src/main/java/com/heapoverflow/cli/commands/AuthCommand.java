package com.heapoverflow.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.heapoverflow.cli.services.AuthServices;
import com.heapoverflow.cli.utils.EnvUtils;

@ShellComponent
public class AuthCommand {
    @ShellMethod(key = "login", value = "Attempt to login")
    public String login() {
        if (EnvUtils.retrieveJwt().equals("")) {
            return AuthServices.attemptGoogleLogin();
        } else {
            return "You are already logged in";
        }
    }

    @ShellMethod(key = "logout", value = "Attempt to logout")
    public String logout() {
        try {
            if (EnvUtils.retrieveJwt().equals("")) {
                return "You are not logged in";
            } else {
                EnvUtils.deleteJWT();
                return "You have been logged out";
            }
        } catch (Exception e) {
            return "Error" + e.getMessage();
        }
    }
}
