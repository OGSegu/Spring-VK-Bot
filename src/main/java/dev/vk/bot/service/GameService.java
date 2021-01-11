package dev.vk.bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vk.bot.controller.MessageSender;
import dev.vk.bot.entities.Game;
import dev.vk.bot.entities.Lobby;
import dev.vk.bot.repositories.GameRepository;
import dev.vk.bot.repositories.LobbyRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Data
@Slf4j
@Service
public class GameService {

    @Autowired
    LobbyRepository lobbyRepo;

    @Autowired
    GameRepository gameRepo;

    @Autowired
    MessageSender messageSender;


    public void sendPreparingStateMsg(Game game) {
        messageSender.sendMessage(lobbyRepo.findByGameId(game.getId()).getPeerId(), String.format(MessageSender.GAME_INFO, game.getCurrentPlayersAmount(), game.getMinPlayersAmount()));
    }

    public Game createGame(int playersAmount, Lobby lobby) {
        Game game = new Game(playersAmount, lobby);
        gameRepo.save(game);
        return game;
    }

    public void startGame() {

    }

    @Bean
    public GameService getGameService() {
        return new GameService();
    }
}
