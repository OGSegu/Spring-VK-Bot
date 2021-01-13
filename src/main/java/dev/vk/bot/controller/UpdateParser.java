package dev.vk.bot.controller;

import dev.vk.bot.entities.Game;
import dev.vk.bot.entities.Lobby;
import dev.vk.bot.entities.Users;
import dev.vk.bot.repositories.LobbyRepository;
import dev.vk.bot.repositories.UsersRepository;
import dev.vk.bot.response.Update;
import dev.vk.bot.response.Update.ReceivedObject.Message.Action;
import dev.vk.bot.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Controller
public class UpdateParser {

    private final String REPLY_MSG = "message_reply";

    @Autowired
    LobbyRepository lobbyRepo;

    @Autowired
    UsersRepository userRepo;

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
        Optional<Users> userOptional = userRepo.findById(userId);
        Users user;
        if (userOptional.isEmpty()) {
            user = usersService.registerUser(userId);
        } else {
            user = userOptional.get();
        }
        if (lobby.isGameRunning()) {
            parseGameCmd(lobby.getGame(), user, peerId, command);
        } else {
            parseMainCmd(peerId, command);
        }
    }

    private void parseMainCmd(int peerId, String command) {
        String[] cmdWithArgs = command.split(" ");
        if (cmdWithArgs.length <= 1) {
            updateExecutor.executeMainCmd(peerId, command);
        } else {
            updateExecutor.executeMultipleArgsCmd(peerId, cmdWithArgs);
        }
    }

    private void parseGameCmd(Game game, Users user, int peerId, String command) {
        switch (game.getState().name()) {
            case ("PREPARING"):
                updateExecutor.executeGamePrepCmd(game, user.getUserId(), peerId, command);
                break;
            case ("STARTING"):
                if (game.getId().equals(user.getCurrentGame().getId())) {
                    updateExecutor.executeGameStartingCmd(game, user.getUserId(), peerId, command);
                }
                break;
            default:
                log.info("Something went wrong");
                break;
        }
    }


    @Bean
    public UpdateParser getCommandParser() {
        return new UpdateParser();
    }
}
