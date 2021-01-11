package dev.vk.bot.service;

import dev.vk.bot.controller.MessageSender;
import dev.vk.bot.entities.Game;
import dev.vk.bot.entities.Lobby;
import dev.vk.bot.entities.Users;
import dev.vk.bot.repositories.GameRepository;
import dev.vk.bot.repositories.LobbyRepository;
import dev.vk.bot.repositories.UsersRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Data
@Slf4j
@Service
public class GameService {

    @Autowired
    LobbyRepository lobbyRepo;

    @Autowired
    GameRepository gameRepo;

    @Autowired
    UsersRepository usersRepo;

    @Autowired
    MessageSender messageSender;

    public void sendStateMsg(int peerId) {
        Lobby lobby = lobbyRepo.findByPeerId(peerId);
        Game game = lobby.getGame();
        String msg = String.format(MessageSender.GAME_INFO, game.getCurrentPlayersAmount(), game.getPlayersToStart());
        if (game.getCurrentPlayersAmount() == game.getPlayersToStart()) {
            msg += "\nВсе игроки набраны. Игра начинается!";
        }
        messageSender.sendMessage(peerId, msg);
    }


    public Game createGame(int playersAmount, Lobby lobby) {
        Game game = new Game(playersAmount, lobby);
        gameRepo.save(game);
        return game;
    }

    public void addParticipant(int peerId, long userId) {
        Game game = lobbyRepo.findByPeerId(peerId).getGame(); // TODO ДВА РАЗА ОТРАБАТЫВАЕТ (строка 37)
        Optional<Users> user = usersRepo.findById(userId);
        if (user.isEmpty()) {
            log.warn("Users can not be found");
            return;
        }
        if (user.get().getCurrentGame() != null) {
            messageSender.sendMessage(game.getLobby().getPeerId(), "Вы уже участвуете в игре");
            return;
        }
        int currentPlayersAmount = game.getCurrentPlayersAmount();
        game.setCurrentPlayersAmount(++currentPlayersAmount);
        gameRepo.save(game);
        user.get().setCurrentGame(game);
        usersRepo.save(user.get());
        sendStateMsg(peerId);
    }

    public void startGame() {

    }

    @Bean
    public GameService getGameService() {
        return new GameService();
    }
}
