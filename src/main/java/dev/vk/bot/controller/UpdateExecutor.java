package dev.vk.bot.controller;


import dev.vk.bot.entities.Game;
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

    /* EVENT */
    private static final String INVITE_EVENT = "chat_invite_user";

    /* ERROR */
    private static final String UNKNOWN_COMMAND = "Ошибка! Такой команды не существует! Введите /помощь";
    private static final String WRONG_ARGS = "Ошибка! Аргументы были введены неверно";

    /* CHAT */
    private static final String WELCOME_IN_CHAT = "Спасибо за приглашение! Предлагаю вам сыграть в викторину.\n Для того чтобы начать введите /создать *кол-во игроков*";

    /* PRIVATE */
    public static final String WELCOME = "Привет, с помощью этого бота, ты можешь поиграть в \"Своя Игра\". Для того чтобы начать, добавь бота в беседу";

    /* OTHER */
    public static final String PONG = "Pong!";

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
                messageSender.sendMessage(peerId, WELCOME);
                break;
            case ("/ping"):
                messageSender.sendMessage(peerId, PONG);
                break;
            case ("/помощь"):
                messageSender.sendMessage(peerId, "тест");
                break;
            default:
                messageSender.sendMessage(peerId, UNKNOWN_COMMAND);
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
                    messageSender.sendMessage(peerId, WRONG_ARGS);
                    return;
                }
                lobbyService.createGameForLobby(peerId, playersAmount);
                break;
            default:
                messageSender.sendMessage(peerId, UNKNOWN_COMMAND);
                break;
        }
    }

    void executeGamePrepCmd(Game game, long userId, int peerId, String command) {
        switch (command) {
            case ("/+"):
                gameService.addParticipant(peerId, userId);
                break;
            case ("/="):
                gameService.sendStateMsg(game, peerId);
                break;
            default:
                messageSender.sendMessage(peerId, UNKNOWN_COMMAND);
                break;
        }
    }

    void executeGameStartingCmd(Game game, long userId, int peerId, String command) {
        String[] cmdWithArgs = command.split(" ");
        switch (cmdWithArgs[0]) {
            case ("/o"):
                gameService.checkAnswer(game, userId, peerId, getAnswer(command));
                break;
            default:
                messageSender.sendMessage(peerId, UNKNOWN_COMMAND);
                break;
        }
    }

    private String getAnswer(String command) {
        return command.substring(3);
    }

    void executeAction(int peerId, String actionType) {
        log.info("Executing action: " + actionType);
        switch (actionType) {
            case (INVITE_EVENT):
                messageSender.sendMessage(peerId, WELCOME_IN_CHAT);
                lobbyService.createLobby(peerId);
                break;
        }
    }

    @Bean
    public UpdateExecutor getCommandExecutor() {
        return new UpdateExecutor();
    }
}
