package com.heapoverflow.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.AuthServices;
import com.heapoverflow.cli.utils.EnvUtils;

@ShellComponent
public class AuthCommand {
    @ShellMethod(key = "login", value = "Attempt to login")
    public String login() {
        if(!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)){
            return AuthServices.attemptGoogleLogin();
        } else {
            return "You are already logged in";
        }
    }

    @ShellMethod(key = "gid", value = "See your google id")
    public String gid() {
        if(EnvUtils.doesKeyExist(EnvConstants.GOOGLE_SUB)){
            return EnvUtils.retrieveValue(EnvConstants.GOOGLE_SUB);
        } else {
            return "You are not logged in or your sub is not set, logout and attempt to login again";
        }
    }

    @ShellMethod(key = "name", value = "See your google name")
    public String name() {
        if(EnvUtils.doesKeyExist(EnvConstants.GOOGLE_NAME)){
            return EnvUtils.retrieveValue(EnvConstants.GOOGLE_NAME);
        } else {
            return "You are not logged in or your sub is not set, logout and attempt to login again";
        }
    }

    @ShellMethod(key = "logout", value = "Attempt to logout")
    public String logout() {
        try {
            if(!EnvUtils.doesKeyExist(EnvConstants.JWT_TOKEN)){
                return "You are not logged in";
            } else {
                EnvUtils.deleteKeys();
                return "You have been logged out";
            }
        } catch (Exception e) {
            return "Error" + e.getMessage();
        }
    }
}
