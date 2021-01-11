package dev.vk.bot.controller;


import dev.vk.bot.service.GameService;
import dev.vk.bot.service.LobbyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import java.util.Arrays;

@Slf4j
@Controller
public class UpdateExecutor {

    private final String INVITE_EVENT = "chat_invite_user";

    @Autowired
    GameService gameService;

    @Autowired
    LobbyService lobbyService;

    @Autowired
    MessageSender messageSender;

    void executeMainCmd(int peerId, String command) {
        log.info("Executing command: " + command);
        switch (command) {
            case ("Начать"):
                messageSender.sendMessage(peerId, MessageSender.WELCOME);
                break;
            case ("/ping"):
                messageSender.sendMessage(peerId, MessageSender.PONG);
                break;
            case ("/помощь"):
                messageSender.sendMessage(peerId, "Помощь нужна всем!");
                break;
            default:
                messageSender.sendMessage(peerId, MessageSender.UNKNOWN_COMMAND);
                break;
        }
    }

    void executeMultipleArgsCmd(int peerId, String[] cmdWithArgs) {
        log.info("Executing command: " + Arrays.toString(cmdWithArgs));
        switch (cmdWithArgs[0]) {
            case ("/создать"):
                int playersAmount;
                try {
                    playersAmount = Integer.parseInt(cmdWithArgs[1]);
                } catch (NumberFormatException e) {
                    messageSender.sendMessage(peerId, MessageSender.WRONG_ARGS);
                    return;
                }
                lobbyService.createGameForLobby(peerId, playersAmount);
                break;
        }
    }

    void executeGameAction(long userId, int peerId) {

    }

    void executeGameCmd(long userId, int peerId, String command) {
        switch (command) {
            case("/+"):
                gameService.addParticipant(peerId, userId);
                break;
            case("/="):
                gameService.sendStateMsg(peerId);
                break;
        }
    }

    void executeAction(int peerId, String actionType) {
        log.info("Executing action: " + actionType);
        switch (actionType) {
            case (INVITE_EVENT):
                messageSender.sendMessage(peerId, MessageSender.WELCOME_IN_CHAT);
                lobbyService.createLobby(peerId);
                break;
        }
    }

    @Bean
    public UpdateExecutor getCommandExecutor() {
        return new UpdateExecutor();
    }
}
