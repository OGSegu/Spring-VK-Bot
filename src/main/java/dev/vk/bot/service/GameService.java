package dev.vk.bot.service;

import dev.vk.bot.controller.MessageSender;
import dev.vk.bot.controller.UpdateExecutor;
import dev.vk.bot.entities.Game;
import dev.vk.bot.entities.Lobby;
import dev.vk.bot.entities.Question;
import dev.vk.bot.entities.Users;
import dev.vk.bot.repositories.GameRepository;
import dev.vk.bot.repositories.LobbyRepository;
import dev.vk.bot.repositories.QuestionRepository;
import dev.vk.bot.repositories.UsersRepository;
import dev.vk.bot.session.GameSession;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@Slf4j
@Service
public class GameService {


    /* ANSWER THREAD */
    Map<Game, GameSession> gameSessionMap = new HashMap<>();


    /* ERROR */
    public static final String ALREADY_IN_GAME = "Вы уже участвуете в игре";

    /* GAME PREPARATION */
    public static final String GAME_INFO = "———Игра———%nКол-во игроков: %d/%d";
    public static final String GAME_READY = "Все игроки набраны. Игра начинается!";
    public static final String GAME_CANCELED = "Игра была отменена.";

    /* GAME PROCESS */
    public static final String WELCOME_TO_GAME = "--- Добро пожаловать на викторину ---%nКоманды:%n%s *ответ* - для того чтобы ответить на вопрос";
    public static final String QUESTION = "❓ Вопрос:%n%s";
    public static final String RIGHT_ANSWER = "✅ %s правильно ответил(а) на вопрос (%d баллов).";
    public static final String WRONG_ANSWER = "\uD83D\uDEAB %s неверно ответил(а) на вопрос.";
    public static final String NO_ANSWER = "Никому не удалось ответить.%nПравильный ответ: %s";
    public static final int RIGHT_ANSWER_RATE = 25;


    private final LobbyRepository lobbyRepo;
    private final GameRepository gameRepo;
    private final UsersRepository usersRepo;
    private final QuestionRepository questionRepo;
    private final MessageSender messageSender;
    private final UsersService usersService;

    public GameService(LobbyRepository lobbyRepo, GameRepository gameRepo, UsersRepository usersRepo, QuestionRepository questionRepo, MessageSender messageSender, UsersService usersService) {
        this.lobbyRepo = lobbyRepo;
        this.gameRepo = gameRepo;
        this.usersRepo = usersRepo;
        this.questionRepo = questionRepo;
        this.messageSender = messageSender;
        this.usersService = usersService;
    }


    public Game getGame(int peerId) {
        return lobbyRepo.findByPeerId(peerId).getGame();
    }

    public boolean isReadyToStart(Game game) {
        return game.getCurrentPlayersAmount() == game.getPlayersToStart();
    }

    public void sendStateMsg(Game game, int peerId) {
        String msg = String.format(GAME_INFO, game.getCurrentPlayersAmount(), game.getPlayersToStart());
        messageSender.sendMessage(peerId, msg);
    }

    private void sendReadyMsg(int peerId) {
        messageSender.sendMessage(peerId, GAME_READY);
    }

    Game createGame(int playersAmount, int maxQuestions, Lobby lobby) {
        Game game = new Game(playersAmount, maxQuestions, lobby);
        gameRepo.save(game);
        return game;
    }

    public void addParticipant(int peerId, long userId) {
        Game game = getGame(peerId);
        Optional<Users> userOptional = usersRepo.findById(userId);
        Users user = usersService.getUserFromOptional(userOptional, userId);
        if (user.getCurrentGame() != null) {
            messageSender.sendMessage(game.getLobby().getPeerId(), ALREADY_IN_GAME);
            return;
        }
        int currentPlayersAmount = game.getCurrentPlayersAmount();

        game.setCurrentPlayersAmount(++currentPlayersAmount);
        gameRepo.save(game);
        user.setCurrentGame(game);
        usersRepo.save(user);

        if (isReadyToStart(game)) {
            sendReadyMsg(peerId);
            startGame(game, peerId);
        } else {
            sendStateMsg(game, peerId);
        }
    }

    public void startGame(Game game, int peerId) {
        game.setState(Game.State.ALIVE);
        gameRepo.save(game);
        messageSender.sendMessage(peerId, String.format(WELCOME_TO_GAME, UpdateExecutor.ANSWER_CMD));
        startGameProcess(game, peerId);
    }

    private void startGameProcess(Game game, int peerId) {
        GameSession gameSession = new GameSession(this, game, peerId, usersRepo.findByCurrentGame(game));
        gameSessionMap.put(game, gameSession);
        gameSessionMap.get(game).start();
    }

    public void stopGame(Game game, int peerId) {
        GameSession gameSession = gameSessionMap.get(game);
        messageSender.sendMessage(peerId, gameSession.getResult());
        addPointsToUsers(gameSession);
        clearDbFromGame(game.getId());
        gameSessionMap.remove(game);
    }

    private void addPointsToUser(Users user, int points) {
        int currentElo = user.getElo();
        user.setElo(currentElo + points);
        usersRepo.save(user);
    }

    private void addPointsToUsers(GameSession gameSession) {
        for (Map.Entry<Users, Integer> userEntry : gameSession.getScoreMap().entrySet()) {
            addPointsToUser(userEntry.getKey(), userEntry.getValue());
        }
    }

    public void cancelGame(long gameId, int peerId) {
        messageSender.sendMessage(peerId, GAME_CANCELED);
        clearDbFromGame(gameId);
    }

    public void clearDbFromGame(long gameId) {
        lobbyRepo.clearGameId(gameId);
        usersRepo.clearUsersFromGame(gameId);
        gameRepo.deleteById(gameId);
    }

    public void checkAnswer(Game game, long userId, int peerId, String answer) {
        Question currentQuestion = game.getCurrentQuestion();
        Optional<Users> userOptional = usersRepo.findById(userId);
        Users user = usersService.getUserFromOptional(userOptional, userId);
        if (currentQuestion != null && currentQuestion.getAnswer().equalsIgnoreCase(answer)) {
            GameSession gameSession = gameSessionMap.get(game);
            Map<Users, Integer> scoreMap = gameSession.getScoreMap();
            int currentScore = scoreMap.get(user) + RIGHT_ANSWER_RATE;
            scoreMap.put(user, currentScore);
            messageSender.sendMessage(peerId, String.format(RIGHT_ANSWER, user.getName(), currentScore));
            gameSession.interrupt();
        } else {
            messageSender.sendMessage(peerId, String.format(WRONG_ANSWER, user.getName()));
        }
    }

}
