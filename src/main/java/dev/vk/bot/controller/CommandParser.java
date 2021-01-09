package dev.vk.bot.controller;

import dev.vk.bot.response.Update.ReceivedObject.Message.Action;
import dev.vk.bot.response.Update;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import java.util.Arrays;

@Slf4j
@Controller
public class CommandParser {

    @Autowired
    CommandExecutor commandExecutor;

    void parseUpdates(Update[] updates) {
        Arrays.stream(updates)
                .filter(update -> !update.getType().equals("message_reply"))
                .forEach(this::parseUpdate);
    }

    private void parseUpdate(Update update) {
        Action action = update.getData().getMessage().getAction();
        int peerId = update.getData().getMessage().getPeerId();
        String command = update.getData().getMessage().getText();
        if (action != null) {
            commandExecutor.executeAction(peerId, action.getType());
        } else {
            parseCommand(peerId, command);
        }
    }

    private void parseCommand(int peerId, String command) {
        String[] cmdWithArgs = command.split(" ");
        if (cmdWithArgs.length <= 1) {
            commandExecutor.executeOneArgCommand(peerId, command);
        } else {
            commandExecutor.executeMultipleArgsCommand(peerId, cmdWithArgs);
        }
    }


    @Bean
    public CommandParser getCommandParser() {
        return new CommandParser();
    }
}
