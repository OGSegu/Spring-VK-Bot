package dev.vk.bot.service;

import dev.vk.bot.controller.MessageSender;
import dev.vk.bot.entities.Game;
import dev.vk.bot.entities.Lobby;
import dev.vk.bot.exception.LobbyCanNotBeFound;
import dev.vk.bot.repositories.LobbyRepository;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Service
public class LobbyService extends VkClient {

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

    public void createGameForLobby(int peerId, int playersAmount) {
        Lobby lobby = lobbyRepo.findByPeerId(peerId);
        if (lobby == null) {
            messageSender.sendMessage(peerId, "Произошла ошибка, лобби не может быть найдено");
            return;
        }
        if (lobby.isGameRunning()) {
            messageSender.sendMessage(peerId, "Набор игроков или игра уже запущена");
            log.info(lobby.getGame().toString());
            return;
        }
        Game game = gameService.createGame(playersAmount, lobby);
        lobby.setGame(game);
        lobbyRepo.save(lobby);
        gameService.sendStateMsg(game);
    }

    public Game getGameInLobby(int peerId) throws LobbyCanNotBeFound {
        Lobby lobby = lobbyRepo.findByPeerId(peerId);
        if (lobby == null) {
            messageSender.sendMessage(peerId, "Произошла ошибка, лобби не может быть найдено");
            throw new LobbyCanNotBeFound();
        }
        return lobby.getGame();
    }


    @Bean
    public LobbyService getLobbyService() {
        return new LobbyService();
    }

}
