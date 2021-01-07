package dev.vk.bot.controller;

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
    MessageSender messageSender;

    void parseCommands(Update[] updates) {
        Arrays.stream(updates)
                .filter(event -> event.getType().equals("message_new"))
                .forEach(event -> parseCommand(event.getObject().getUserId(), event.getObject().getBody()));
    }

    void parseCommand(int userId, String command) {
        switch (command) {
            case ("/помощь"):
                messageSender.sendMessage(userId, "Помощь нужна всем!");
                break;
            default:
                messageSender.sendMessage(userId, "Такой команды не существует");
                break;
        }
    }

    @Bean
    public CommandParser getCommandParser() {
        return new CommandParser();
    }
}
