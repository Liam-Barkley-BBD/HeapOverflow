package com.heapoverflow.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;
import org.springframework.shell.table.TableModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heapoverflow.cli.services.ThreadsService;
import com.heapoverflow.cli.utils.EnvUtils;

@ShellComponent
public class ThreadCommands {

    @ShellMethod(key = "threads", value = "Get threads")
    public String threads() throws Exception {
        if (EnvUtils.retrieveJwt().equals("")) {
            return "You are not logged, please login!";
        } else {
            String jsonResponse = ThreadsService.getThreads().toString();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode contentArray = rootNode.get("content");

            if (contentArray == null || !contentArray.isArray()) {
                return "No threads found.";
            }

            TableModelBuilder<String> modelBuilder = new TableModelBuilder<>();
            modelBuilder.addRow().addValue("ID").addValue("Title").addValue("Created At").addValue("Closed At");

            for (JsonNode thread : contentArray) {
                modelBuilder.addRow()
                        .addValue(thread.get("id").asText())
                        .addValue(thread.get("title").asText())
                        .addValue(thread.get("createdAt").asText())
                        .addValue(thread.get("closedAt").isNull() ? "Open" : thread.get("closedAt").asText());
            }

            TableModel tableModel = modelBuilder.build();
            TableBuilder tableBuilder = new TableBuilder(tableModel);
            tableBuilder.addFullBorder(BorderStyle.oldschool);

            return tableBuilder.build().render(120);
        }
    }
}