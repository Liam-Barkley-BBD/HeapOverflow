package com.heapoverflow.utils;

public class TextUtils {
    public static String getWelcomeMessage(){
        return          "\r\n" + //
                        " /$$   /$$                                      /$$$$$$                                /$$$$$$$$ /$$                        \r\n" +
                        "| $$  | $$                                     /$$__  $$                              | $$_____/| $$                        \r\n" +
                        "| $$  | $$  /$$$$$$   /$$$$$$   /$$$$$$       | $$  \\ $$ /$$    /$$ /$$$$$$   /$$$$$$ | $$      | $$  /$$$$$$  /$$  /$$  /$$\r\n" +
                        "| $$$$$$$$ /$$__  $$ |____  $$ /$$__  $$      | $$  | $$|  $$  /$$//$$__  $$ /$$__  $$| $$$$$   | $$ /$$__  $$| $$ | $$ | $$\r\n" +
                        "| $$__  $$| $$$$$$$$  /$$$$$$$| $$  \\ $$      | $$  | $$ \\  $$/$$/| $$$$$$$$| $$  \\__/| $$__/   | $$| $$  \\ $$| $$ | $$ | $$\r\n" +
                        "| $$  | $$| $$_____/ /$$__  $$| $$  | $$      | $$  | $$  \\  $$$/ | $$_____/| $$      | $$      | $$| $$  | $$| $$ | $$ | $$\r\n" +
                        "| $$  | $$|  $$$$$$$|  $$$$$$$| $$$$$$$/      |  $$$$$$/   \\  $/  |  $$$$$$$| $$      | $$      | $$|  $$$$$$/|  $$$$$/$$$$/\r\n" +
                        "|__/  |__/ \\_______/ \\_______/| $$____/        \\______/     \\_/    \\_______/|__/      |__/      |__/ \\______/  \\_____/\\___/ \r\n" +
                        "                              | $$                                                                                          \r\n" +
                        "                              | $$                                                                                          \r\n" +
                        "                              |__/                                                                                          \r\n" +
                        "\r\n" +
                        "             | / _   \"Your Stack is Overflowing!\" 📚🔥\r\n" +
                        "------------------------------------------------------------\r\n" +
                        " HeapOverflow CLI - Because debugging alone is no fun! 🐛\r\n" +
                        " Version: 1.0.0\r\n" +
                        " Running on: "+ SystemUtils.getJavaVersion() + " | JLine3 Powered\r\n" +
                        " OS:        "+ SystemUtils.getOsName() + " " + SystemUtils.getOsVersion()  + "\r\n" +
                        " Uptime:    "+ SystemUtils.getUptime() + "\r\n" +
                        "------------------------------------------------------------\r\n" +
                        "\r\n" +
                        " Type 'help' to see available commands or 'exit' to quit.\r\n";
    }

    public static String getByeMessage(){
        return          "GoodBye and have a great day! from \r\n\r\n" + //
                        " /$$   /$$                                      /$$$$$$                                /$$$$$$$$ /$$                        \r\n" +
                        "| $$  | $$                                     /$$__  $$                              | $$_____/| $$                        \r\n" +
                        "| $$  | $$  /$$$$$$   /$$$$$$   /$$$$$$       | $$  \\ $$ /$$    /$$ /$$$$$$   /$$$$$$ | $$      | $$  /$$$$$$  /$$  /$$  /$$\r\n" +
                        "| $$$$$$$$ /$$__  $$ |____  $$ /$$__  $$      | $$  | $$|  $$  /$$//$$__  $$ /$$__  $$| $$$$$   | $$ /$$__  $$| $$ | $$ | $$\r\n" +
                        "| $$__  $$| $$$$$$$$  /$$$$$$$| $$  \\ $$      | $$  | $$ \\  $$/$$/| $$$$$$$$| $$  \\__/| $$__/   | $$| $$  \\ $$| $$ | $$ | $$\r\n" +
                        "| $$  | $$| $$_____/ /$$__  $$| $$  | $$      | $$  | $$  \\  $$$/ | $$_____/| $$      | $$      | $$| $$  | $$| $$ | $$ | $$\r\n" +
                        "| $$  | $$|  $$$$$$$|  $$$$$$$| $$$$$$$/      |  $$$$$$/   \\  $/  |  $$$$$$$| $$      | $$      | $$|  $$$$$$/|  $$$$$/$$$$/\r\n" +
                        "|__/  |__/ \\_______/ \\_______/| $$____/        \\______/     \\_/    \\_______/|__/      |__/      |__/ \\______/  \\_____/\\___/ \r\n" +
                        "                              | $$                                                                                          \r\n" +
                        "                              | $$                                                                                          \r\n" +
                        "                              |__/                                                                                          \r\n";
    }

    public static String getHelpMenuNoAuth(){
        return "Usage: [command] [options]\r\n" + //
                "\r\n" + //
                "Commands:\r\n" + //
                "  login           Login to your google account\r\n" + //
                "  list            List questions\r\n" + //
                "  search          Search for a question or answer\r\n" + //
                "  help            Show help information about commands\r\n" + //
                "\r\n" + //
                "Examples:\r\n" + //
                " login \r\n" + //
                " list --all\r\n" + //
                " search \"CLI app\"\r\n" + //
                        "\r\n" + //
                "For more information, visit the documentation at https://docs.heapoverflow.com";
    }

    public static String getHelpMenuAuth(){
        return "Usage: [command] [options]\r\n" + //
                "\r\n" + //
                "Commands:\r\n" + //
                "  ask             Ask a new question\r\n" + //
                "  answer          Answer an existing question\r\n" + //
                "  list            List questions\r\n" + //
                "  vote            Upvote or downvote a question/answer\r\n" + //
                "  search          Search for a question or answer\r\n" + //
                "  edit            Edit your question/answer\r\n" + //
                "  delete          Delete your question/answer\r\n" + //
                "  help            Show help information about commands\r\n" + //
                "\r\n" + //
                "Examples:\r\n" + //
                " ask \"How do I create a CLI app in Node.js?\"\r\n" + //
                " answer 1234 \"You can use the Commander.js library for creating CLIs.\"\r\n" + //
                " list --all\r\n" + //
                " search \"CLI app\"\r\n" + //
                        "\r\n" + //
                "For more information, visit the documentation at https://docs.heapoverflow.com";
    }

    public static String getTerminalPrompt(){
        return "HeapOverflow.CLI_> ";
    }
}
