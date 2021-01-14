package dev.vk.bot.service;

import dev.vk.bot.controller.MessageSender;
import dev.vk.bot.entities.Game;
import dev.vk.bot.entities.Lobby;
import dev.vk.bot.repositories.LobbyRepository;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Service
public class LobbyService extends VkClient {

    private static final String GAME_IS_RUNNING = "Набор игроков или игра уже запущена";
    private static final String NO_LOBBY_FOUND = "Произошла ошибка, лобби не может быть найдено";

    @Autowired
    MessageSender messageSender;

    @Autowired
    GameService gameService;

    @Autowired
    LobbyRepository lobbyRepo;

    public static final int PEER_NUMBER = 2000000000;

    public void createLobby(int peerId) {
        log.info("Creating lobby");
        int chatId = peerId - PEER_NUMBER;
        Lobby lobby = new Lobby(chatId, peerId);
        lobbyRepo.save(lobby);
        log.info("Lobby was successfully saved");
    }

    public void createGameForLobby(int peerId, int playersAmount, int maxQuestions) {
        Lobby lobby = lobbyRepo.findByPeerId(peerId);
        if (lobby == null) {
            messageSender.sendMessage(peerId, NO_LOBBY_FOUND);
            return;
        }
        if (lobby.isGameRunning()) {
            messageSender.sendMessage(peerId, GAME_IS_RUNNING);
            log.info(lobby.getGame().toString());
            return;
        }
        Game game = gameService.createGame(playersAmount, maxQuestions, lobby);
        lobby.setGame(game);
        lobbyRepo.save(lobby);
        gameService.sendStateMsg(game, peerId);
    }

}
