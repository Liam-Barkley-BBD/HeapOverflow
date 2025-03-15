package com.heapoverflow.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.heapoverflow.cli.constants.EnvConstants;
import com.heapoverflow.cli.services.ApiService;
import com.heapoverflow.cli.utils.EnvUtils;

@ShellComponent
public class ApiCommand {
    @ShellMethod(key = "replies", value = "Get replies")
    public String replies() {
        if(!EnvUtils.doesEnvExist(EnvConstants.JWT)){
            return "You are not logged, please login!";
        } else{
            return ApiService.getReplies().toString();
        }
    }
}
