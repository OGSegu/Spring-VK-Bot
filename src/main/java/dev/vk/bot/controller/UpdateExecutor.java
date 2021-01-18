package dev.vk.bot.controller;


import dev.vk.bot.entities.Game;
import dev.vk.bot.service.GameService;
import dev.vk.bot.service.LobbyService;
import dev.vk.bot.service.UsersService;
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
    private static final String UNKNOWN_CMD = "Ошибка! Такой команды не существует! Введите %s";
    private static final String WRONG_ARGS = "Ошибка! Аргументы были введены неверно";

    /* CHAT */
    private static final String WELCOME_IN_CHAT = "Спасибо за приглашение! Предлагаю вам сыграть в викторину.\n Для того чтобы начать введите /создать *кол-во игроков* *кол-во вопросов*";
    public static final String HELP_MSG = "Команды:%n" +
            "%s *кол-во игроков* *кол-во вопросов* - создать игру%n" +
            "%s - участвовать в игре%n" +
            "%s - получить информацию о стадии игры%n" +
            "%s *ответ* - ответить на вопрос";
    /* PRIVATE */
    public static final String WELCOME = "Привет, с помощью этого бота, ты можешь поиграть в \"Своя Игра\". Для того чтобы начать, добавь бота в беседу";

    /* OTHER */
    public static final String PONG = "Pong!";

    /* COMMAND */
    public static final String ANSWER_CMD = "/=";
    public static final String PARTICIPANT_CMD = "/go";
    public static final String INFO_CMD = "/i";
    public static final String START_MSG = "Начать";
    public static final String PING_CMD = "/ping";
    public static final String HELP_CMD = "/помощь";
    public static final String CREATE_CMD = "/создать";

    @Autowired
    GameService gameService;

    @Autowired
    LobbyService lobbyService;

    @Autowired
    UsersService usersService;

    @Autowired
    MessageSender messageSender;

    void executeMainCmd(int peerId, String command) {
        log.info("Executing command: " + command);
        switch (command) {
            case START_MSG:
                messageSender.sendMessage(peerId, WELCOME);
                break;
            case PING_CMD:
                messageSender.sendMessage(peerId, PONG);
                break;
            case HELP_CMD:
                messageSender.sendMessage(peerId, String.format(HELP_MSG, CREATE_CMD, PARTICIPANT_CMD, INFO_CMD, ANSWER_CMD));
                break;
            default:
                messageSender.sendMessage(peerId, String.format(UNKNOWN_CMD, HELP_CMD));
                break;
        }
    }

    void executeMultipleArgsCmd(int peerId, String[] cmdWithArgs) {
        log.info("Executing command: " + Arrays.toString(cmdWithArgs));
        switch (cmdWithArgs[0]) {
            case CREATE_CMD:
                int playersAmount;
                int maxQuestions;
                try {
                    playersAmount = Integer.parseInt(cmdWithArgs[1]);
                    maxQuestions = Integer.parseInt(cmdWithArgs[2]);
                } catch (Exception e) {
                    messageSender.sendMessage(peerId, WRONG_ARGS);
                    return;
                }
                lobbyService.createGameForLobby(peerId, playersAmount, maxQuestions);
                break;
            default:
                messageSender.sendMessage(peerId, String.format(UNKNOWN_CMD, HELP_CMD));
                break;
        }
    }

    void executeGamePrepCmd(Game game, long userId, int peerId, String command) {
        switch (command) {
            case PARTICIPANT_CMD:
                gameService.addParticipant(peerId, userId);
                break;
            case INFO_CMD:
                gameService.sendStateMsg(game, peerId);
                break;
            default:
                messageSender.sendMessage(peerId, String.format(UNKNOWN_CMD, HELP_CMD));
                break;
        }
    }

    void executeGameStartingCmd(Game game, long userId, int peerId, String command) {
        String[] cmdWithArgs = command.split(" ");
        switch (cmdWithArgs[0]) {
            case (ANSWER_CMD):
                gameService.checkAnswer(game, userId, peerId, getAnswer(command));
                break;
            default:
                messageSender.sendMessage(peerId, String.format(UNKNOWN_CMD, HELP_CMD));
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
            default:
                log.info("Unknown event was received");
                break;
        }
    }

    @Bean
    public UpdateExecutor getCommandExecutor() {
        return new UpdateExecutor();
    }
}
