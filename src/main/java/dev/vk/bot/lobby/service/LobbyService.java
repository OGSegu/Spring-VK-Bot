package dev.vk.bot.lobby.service;

import dev.vk.bot.component.MessageSender;
import dev.vk.bot.config.ChatAPI;
import dev.vk.bot.config.Config;
import dev.vk.bot.entities.Game;
import dev.vk.bot.entities.Lobby;
import dev.vk.bot.game.service.GameService;
import dev.vk.bot.repositories.LobbyRepository;
import dev.vk.bot.service.LobbyValidator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static dev.vk.bot.controller.UpdateExecutor.WELCOME_IN_CHAT;

@Slf4j
@Data
@Service
public class LobbyService {

    private static final String GAME_IS_RUNNING = "Набор игроков или игра уже запущена";
    private static final String NO_LOBBY_FOUND = "Произошла ошибка, лобби не может быть найдено";

    private final MessageSender messageSender;
    private final GameService gameService;
    private final LobbyRepository lobbyRepo;
    private final LobbyValidator lobbyValidator;
    private final ChatAPI chatAPI;
    private final Config mainConfig;
    private final RestTemplate restTemplate;

    public static final int PEER_NUMBER = 2000000000;

    public LobbyService(MessageSender messageSender, GameService gameService, LobbyRepository lobbyRepo, LobbyValidator lobbyValidator, ChatAPI chatAPI, Config mainConfig, RestTemplate restTemplate) {
        this.messageSender = messageSender;
        this.gameService = gameService;
        this.lobbyRepo = lobbyRepo;
        this.lobbyValidator = lobbyValidator;
        this.chatAPI = chatAPI;
        this.mainConfig = mainConfig;
        this.restTemplate = restTemplate;
    }

    public void createLobby(int peerId) {
        new Thread(() -> {
            messageSender.sendMessage(peerId, "У вас есть 15 секунд, что бы сделать бота администратором");
            if (!lobbyValidator.waitForAdmin(peerId)) {
                messageSender.sendMessage(peerId, "Вы не успели. Исключите бота из беседы и добавьте снова.");
                return;
            }
            messageSender.sendMessage(peerId, WELCOME_IN_CHAT);
            int chatId = peerId - PEER_NUMBER;
            Lobby lobby = new Lobby(chatId, peerId);
            lobbyRepo.save(lobby);
            log.debug("Lobby was successfully saved");
        }).start();
    }

    public void createGameForLobby(int peerId, int playersAmount, int maxQuestions) {
        Lobby lobby = lobbyRepo.findByPeerId(peerId);
        if (lobby == null) {
            messageSender.sendMessage(peerId, NO_LOBBY_FOUND);
            return;
        }
        if (lobby.isGameRunning()) {
            messageSender.sendMessage(peerId, GAME_IS_RUNNING);
            return;
        }
        Game game = gameService.createGame(playersAmount, maxQuestions, lobby);
        lobby.setGame(game);
        lobbyRepo.save(lobby);
        gameService.sendStateMsg(game, peerId);
    }

}
