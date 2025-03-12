package com.heapoverflow.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class HelloCommand {
    @ShellMethod(key = "hello", value = "Say hello")
    public String hello(@ShellOption(value = "name", defaultValue = "world") String name) {
        return "Hello, " + name + "!";
    }

    @ShellMethod(key = "goodbye", value = "Say goodbye")
    public String goodbye() {
        return "Goodbye!";
    }
}