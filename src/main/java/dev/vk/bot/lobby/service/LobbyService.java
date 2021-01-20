package dev.vk.bot.lobby.service;

import dev.vk.bot.component.MessageSender;
import dev.vk.bot.entities.Game;
import dev.vk.bot.entities.Lobby;
import dev.vk.bot.game.service.GameService;
import dev.vk.bot.repositories.LobbyRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Data
@Service
public class LobbyService {

    private static final String GAME_IS_RUNNING = "Набор игроков или игра уже запущена";
    private static final String NO_LOBBY_FOUND = "Произошла ошибка, лобби не может быть найдено";

    private final MessageSender messageSender;
    private final GameService gameService;
    private final LobbyRepository lobbyRepo;

    public static final int PEER_NUMBER = 2000000000;

    public LobbyService(MessageSender messageSender, GameService gameService, LobbyRepository lobbyRepo) {
        this.messageSender = messageSender;
        this.gameService = gameService;
        this.lobbyRepo = lobbyRepo;
    }

    public void createLobby(int peerId) {
        log.debug("Creating lobby");
        int chatId = peerId - PEER_NUMBER;
        Lobby lobby = new Lobby(chatId, peerId);
        lobbyRepo.save(lobby);
        log.debug("Lobby was successfully saved");
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
