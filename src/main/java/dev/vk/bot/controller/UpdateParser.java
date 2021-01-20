package dev.vk.bot.controller;

import dev.vk.bot.entities.Game;
import dev.vk.bot.entities.Lobby;
import dev.vk.bot.entities.Users;
import dev.vk.bot.response.Update;
import dev.vk.bot.response.Update.ReceivedObject.Message.Action;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@org.springframework.stereotype.Controller
public class UpdateParser {

    private static final String REPLY = "message_reply";

    private final Controller controller;
    private final UpdateExecutor updateExecutor;

    public UpdateParser(Controller controller, UpdateExecutor updateExecutor) {
        this.controller = controller;
        this.updateExecutor = updateExecutor;
    }

    void parseUpdates(Update[] updates) {
        Arrays.stream(updates)
                .filter(update -> !update.getType().equals(REPLY))
                .forEach(this::parseUpdate);
    }

    private void parseUpdate(Update update) {
        Action action = update.getData().getMessage().getAction();
        int peerId = update.getData().getMessage().getPeerId();
        int userId = update.getData().getMessage().getFromId();
        if (!controller.usersService.userExists(userId)) {
            controller.usersService.registerUser(userId);
        }
        String command = update.getData().getMessage().getText();
        if (action != null) {
            updateExecutor.executeAction(peerId, action.getType());
        } else {
            parseCommand(userId, peerId, command);
        }
    }


    private void parseCommand(long userId, int peerId, String command) {
        Lobby lobby = controller.lobbyRepo.findByPeerId(peerId);
        Optional<Users> userOptional = controller.usersRepo.findById(userId);
        Users user = controller.usersService.getUserFromOptional(userOptional, userId);
        if (lobby == null) {
            return;
        }
        if (lobby.isGameRunning()) {
            parseGameCmd(lobby.getGame(), user, peerId, command);
        } else {
            parseMainCmd(peerId, userId, command);
        }
    }

    private void parseMainCmd(int peerId, long userId,  String command) {
        String[] cmdWithArgs = command.split(" ");
        if (cmdWithArgs.length <= 1) {
            updateExecutor.executeMainCmd(peerId, userId, command);
        } else {
            updateExecutor.executeMultipleArgsCmd(peerId, cmdWithArgs);
        }
    }

    private void parseGameCmd(Game game, Users user, int peerId, String command) {
        switch (game.getState().name()) {
            case ("PREPARING"):
                updateExecutor.executeGamePrepCmd(game, user.getUserId(), peerId, command);
                break;
            case ("ALIVE"):
                if (game.getId().equals(user.getCurrentGame().getId())) {
                    updateExecutor.executeGameStartingCmd(game, user.getUserId(), peerId, command);
                }
                break;
            default:
                log.info("Unknown event was received");
                break;
        }
    }
}
