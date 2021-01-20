package dev.vk.bot.controller;

import dev.vk.bot.component.MessageSender;
import dev.vk.bot.game.service.GameService;
import dev.vk.bot.lobby.service.LobbyService;
import dev.vk.bot.repositories.GameRepository;
import dev.vk.bot.repositories.LobbyRepository;
import dev.vk.bot.repositories.QuestionRepository;
import dev.vk.bot.repositories.UsersRepository;
import dev.vk.bot.service.UsersService;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@org.springframework.stereotype.Controller
public abstract class Controller {

    protected final GameService gameService;
    protected final LobbyService lobbyService;
    protected final UsersService usersService;
    protected final MessageSender messageSender;
    protected final GameRepository gameRepo;
    protected final LobbyRepository lobbyRepo;
    protected final UsersRepository usersRepo;
    protected final QuestionRepository questionRepo;

    Controller (Controller controller) {
        this.gameService = controller.gameService;
        this.lobbyService = controller.lobbyService;
        this.usersService = controller.usersService;
        this.messageSender = controller.messageSender;
        this.gameRepo = controller.gameRepo;
        this.lobbyRepo = controller.lobbyRepo;
        this.usersRepo = controller.usersRepo;
        this.questionRepo = controller.questionRepo;
    }

}
