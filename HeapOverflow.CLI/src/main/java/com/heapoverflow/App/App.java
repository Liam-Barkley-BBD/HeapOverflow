package com.heapoverflow.App;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import com.heapoverflow.constants.CommandConstants;
import com.heapoverflow.gui.Gui;
import com.heapoverflow.utils.TextUtils;

public class App {
    private static final LineReader reader = LineReaderBuilder.builder().build();

    public static void init(){
        Gui.renderWelcomeText();
    }

    public static void run(){
        while (true) {
            String input = reader.readLine(TextUtils.getTerminalPrompt());
            
            switch (input.trim().toLowerCase()) {
                case CommandConstants.HELP:
                    Gui.renderHelpMenu();
                    break;
                case CommandConstants.QUIT:
                    Gui.renderBye();
                    return;
                case CommandConstants.LOGIN:
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public static void cleanUp(){

    }
}
