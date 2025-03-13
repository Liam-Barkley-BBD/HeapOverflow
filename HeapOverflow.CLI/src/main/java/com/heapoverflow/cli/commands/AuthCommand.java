package com.heapoverflow.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class AuthCommand {
    @ShellMethod(key = "login", value = "Attempt to login")
    public String login() {
        return "Hello, ";
    }
}
