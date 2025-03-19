package com.heapoverflow.cli.utils;

import java.util.Optional;

import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

public class TextUtils {
    public static String renderTable(TableModel model) {
        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addFullBorder(BorderStyle.oldschool);
        return tableBuilder.build().render(120);
    }

    public static String nullToEmpty(String input) {
        return Optional.ofNullable(input).orElse("");
    }
}
