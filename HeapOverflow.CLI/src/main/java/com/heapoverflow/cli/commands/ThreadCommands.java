package com.heapoverflow.cli.commands;

import java.util.Objects;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.heapoverflow.cli.services.ThreadsSevice;
import com.heapoverflow.cli.utils.EnvUtils;

@ShellComponent
public class ThreadCommands {

    @ShellMethod(key = "threads", value = "Get threads")
    public String threads() {
        if (EnvUtils.retrieveJwt().equals("")) {
            return "You are not logged, please login!";
        } else {
           ThreadsSevice.getThreads();
            return "";
        }
    }
}
