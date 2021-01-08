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
    MessageSender messageSender;

    void parseCommands(Update[] updates) {
        Arrays.stream(updates)
                .filter(update -> !update.getType().equals("message_reply"))
                .forEach(this::parseCommand);
    }

    void parseCommand(Update update) {
        Action action = update.getData().getMessage().getAction();
        int peerId = update.getData().getMessage().getPeerId();
        String command = update.getData().getMessage().getText();
        if (action != null) {
            answerToAction(peerId, action.getType());
        } else {
            answerToCommand(peerId, command);
        }
    }

    private void answerToCommand(int peerId, String command) {
        switch (command) {
            case ("Начать"):
                messageSender.sendMessage(peerId, MessageSender.WELCOME_MSG);
                break;
            case ("/ping"):
                messageSender.sendMessage(peerId, MessageSender.PONG_MSG);
                break;
            case ("/помощь"):
                messageSender.sendMessage(peerId, "Помощь нужна всем!");
                break;
            default:
                messageSender.sendMessage(peerId, MessageSender.NOCOMMAND_MSG);
                break;
        }
    }

    private void answerToAction(int peerId, String type) {
        switch (type) {
            case ("chat_invite_user"):
                messageSender.sendMessage(peerId, "Thanks for invite");
                break;
        }
    }

    @Bean
    public CommandParser getCommandParser() {
        return new CommandParser();
    }
}
