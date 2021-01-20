package dev.vk.bot.controller;


import dev.vk.bot.entities.Game;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@org.springframework.stereotype.Controller
public class UpdateExecutor {

    private final Controller controller;

    /* EVENT */
    private static final String INVITE_EVENT = "chat_invite_user";

    /* ERROR */
    private static final String UNKNOWN_CMD = "Ошибка! Такой команды не существует! Введите %s";
    private static final String WRONG_ARGS = "Ошибка! Аргументы были введены неверно";

    /* CHAT */
    public static final String WELCOME_IN_CHAT = "Спасибо за приглашение! Предлагаю вам сыграть в викторину.\n Для того чтобы начать введите /создать *кол-во игроков* *кол-во вопросов*";
    public static final String HELP_MSG = "-Общие-%n" +
            "%s - получить информацию о себе%n" +
            "-Создание игры-%n" +
            "%s *кол-во игроков* *кол-во вопросов* - создать игру%n" +
            "%s - отменить игру%n" +
            "%s - участвовать в игре%n" +
            "%s - получить информацию о стадии игры%n" +
            "-Игровой процесс-%n" +
            "%s *ответ* - ответить на вопрос%n";
    /* PRIVATE */
    public static final String WELCOME = "Привет, с помощью этого бота, ты можешь поиграть в \"Своя Игра\". Для того чтобы начать, добавь бота в беседу";

    /* OTHER */
    public static final String PONG = "Pong!";

    /* COMMAND */
    public static final String ANSWER_CMD = "/=";
    public static final String PARTICIPANT_CMD = "/го";
    public static final String GAMEINFO_CMD = "/игра";
    public static final String START_MSG = "Начать";
    public static final String PING_CMD = "/ping";
    public static final String HELP_CMD = "/помощь";
    public static final String CREATE_CMD = "/создать";
    public static final String CANCEL_CMD = "/отмена";
    public static final String MYINFO_CMD = "/я";

    public UpdateExecutor(Controller controller) {
        this.controller = controller;
    }

    void executeMainCmd(int peerId, long userId, String command) {
        switch (command) {
            case START_MSG:
                controller.messageSender.sendMessage(peerId, WELCOME);
                break;
            case PING_CMD:
                controller.messageSender.sendMessage(peerId, PONG);
                break;
            case HELP_CMD:
                controller.messageSender.sendMessage(peerId, String.format(HELP_MSG,
                        MYINFO_CMD,
                        CREATE_CMD,
                        CANCEL_CMD,
                        PARTICIPANT_CMD,
                        GAMEINFO_CMD,
                        ANSWER_CMD)
                );
                break;
            case MYINFO_CMD:
                controller.messageSender.sendMessage(peerId, controller.usersService.getUserInfoText(userId));
                break;
            default:
                controller.messageSender.sendMessage(peerId, String.format(UNKNOWN_CMD, HELP_CMD));
                break;
        }
    }

    void executeMultipleArgsCmd(int peerId, String[] cmdWithArgs) {
        switch (cmdWithArgs[0]) {
            case CREATE_CMD:
                int playersAmount;
                int maxQuestions;
                try {
                    playersAmount = Integer.parseInt(cmdWithArgs[1]);
                    maxQuestions = Integer.parseInt(cmdWithArgs[2]);
                } catch (Exception e) {
                    controller.messageSender.sendMessage(peerId, WRONG_ARGS);
                    return;
                }
                controller.lobbyService.createGameForLobby(peerId, playersAmount, maxQuestions);
                break;
            default:
                controller.messageSender.sendMessage(peerId, String.format(UNKNOWN_CMD, HELP_CMD));
                break;
        }
    }

    void executeGamePrepCmd(Game game, long userId, int peerId, String command) {
        switch (command) {
            case PARTICIPANT_CMD:
                controller.gameService.addParticipant(peerId, userId);
                break;
            case GAMEINFO_CMD:
                controller.gameService.sendStateMsg(game, peerId);
                break;
            case CANCEL_CMD:
                controller.gameService.cancelGame(game.getId(), peerId);
                break;
            default:
                controller.messageSender.sendMessage(peerId, String.format(UNKNOWN_CMD, HELP_CMD));
                break;
        }
    }

    void executeGameStartingCmd(Game game, long userId, int peerId, String command) {
        String[] cmdWithArgs = command.split(" ");
        switch (cmdWithArgs[0]) {
            case (ANSWER_CMD):
                controller.gameService.checkAnswer(game, userId, peerId, getAnswer(command));
                break;
            default:
                controller.messageSender.sendMessage(peerId, String.format(UNKNOWN_CMD, HELP_CMD));
                break;
        }
    }

    private String getAnswer(String command) {
        return command.substring(3);
    }

    void executeAction(int peerId, String actionType) {
        log.debug("Executing action: " + actionType);
        switch (actionType) {
            case (INVITE_EVENT):
                controller.lobbyService.createLobby(peerId);
                break;
            default:
                log.info("Unknown event was received");
                break;
        }
    }
}
