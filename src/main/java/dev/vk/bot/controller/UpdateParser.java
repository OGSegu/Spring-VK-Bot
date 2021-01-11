package dev.vk.bot.controller;

import dev.vk.bot.response.Update;
import dev.vk.bot.response.Update.ReceivedObject.Message.Action;
import dev.vk.bot.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
public class UpdateParser {

    private static final List<String> GAME_CMDS = Arrays.asList(
            "/+",
            "/="
    );

    private static final List<String> MAIN_CMDS = Arrays.asList(
            "/ping",
            "/помощь",
            "/создать"
    );

    private final String REPLY_MSG = "message_reply";

    @Autowired
    UsersService usersService;

    @Autowired
    UpdateExecutor updateExecutor;

    void parseUpdates(Update[] updates) {
        Arrays.stream(updates)
                .filter(update -> !update.getType().equals(REPLY_MSG))
                .forEach(this::parseUpdate);
    }

    private void parseUpdate(Update update) {
        Action action = update.getData().getMessage().getAction();
        int peerId = update.getData().getMessage().getPeerId();
        int userId = update.getData().getMessage().getFromId();
        initUser(userId);
        String command = update.getData().getMessage().getText();
        if (action != null) {
            updateExecutor.executeAction(peerId, action.getType());
        } else {
            parseCommand(userId, peerId, command);
        }
    }

    private void initUser(long userId) {
        if (!usersService.userExists(userId)) {
            usersService.registerUser(userId);
            log.info("User was successfully registered");
        }
    }

    private void parseCommand(long userId, int peerId, String command) {
        String[] cmdWithArgs = command.split(" ");
        if (GAME_CMDS.contains(command)) {
            updateExecutor.executeGameCmd(userId, peerId, command);
        } else {
            if (cmdWithArgs.length <= 1) {
                updateExecutor.executeMainCmd(peerId, command);
            } else {
                updateExecutor.executeMultipleArgsCmd(peerId, cmdWithArgs);
            }
        }
    }


    @Bean
    public UpdateParser getCommandParser() {
        return new UpdateParser();
    }
}
