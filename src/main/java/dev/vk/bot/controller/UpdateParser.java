package dev.vk.bot.controller;

import dev.vk.bot.component.MessageSender;
import dev.vk.bot.entities.Game;
import dev.vk.bot.entities.Lobby;
import dev.vk.bot.entities.Users;
import dev.vk.bot.game.service.GameService;
import dev.vk.bot.lobby.service.LobbyService;
import dev.vk.bot.repositories.UsersRepository;
import dev.vk.bot.response.Update;
import dev.vk.bot.response.Update.ReceivedObject.Message.Action;
import dev.vk.bot.service.UsersService;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@org.springframework.stereotype.Controller
public class UpdateParser extends Controller {

    private static final String REPLY = "message_reply";

    private final UpdateExecutor updateExecutor;

    public UpdateParser(GameService gameService, LobbyService lobbyService,
                        UsersService usersService, MessageSender messageSender,
                        UpdateExecutor updateExecutor, UsersRepository usersRepo) {
        super(Controller.builder()
                .gameService(gameService)
                .lobbyService(lobbyService)
                .usersService(usersService)
                .messageSender(messageSender)
                .usersRepo(usersRepo)
                .build()
        );
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
        if (!usersService.userExists(userId)) {
            usersService.registerUser(userId);
        }
        String command = update.getData().getMessage().getText();
        if (action != null) {
            updateExecutor.executeAction(peerId, action.getType());
        } else {
            parseCommand(userId, peerId, command);
        }
    }


    private void parseCommand(long userId, int peerId, String command) {
        Lobby lobby = lobbyRepo.findByPeerId(peerId);
        Optional<Users> userOptional = usersRepo.findById(userId);
        Users user = usersService.getUserFromOptional(userOptional, userId);
        if (lobby == null) {
            parseMainCmd(peerId, userId, command);
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
