package dev.vk.bot.service;

import dev.vk.bot.controller.MessageSender;
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
    public static final String GAME_INFO = "------Игра------%nКол-во игроков: %d/%d";
    public static final String GAME_READY = "Все игроки набраны. Игра начинается!";

    /* GAME PROCESS */
    public static final String WELCOME_TO_GAME = "--- Добро пожаловать на викторину ---\nКоманды:\n/o *ответ* - для того чтобы ответить на вопрос";
    public static final String QUESTION = "Вопрос:%n%s";
    public static final String RIGHT_ANSWER = "%d правильно ответил(а) на вопрос. Продолжаем.";
    public static final String WRONG_ANSWER = "%d неверно ответил(а) на вопрос.";
    public static final String NO_ANSWER = "Никому не удалось ответить.%nПравильный ответ: %s";


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
        messageSender.sendMessage(peerId, WELCOME_TO_GAME);
        startGameProcess(game, peerId);
    }

    public void startGameProcess(Game game, int peerId) {
        GameSession gameSession = new GameSession(this, game, peerId);
        gameSessionMap.put(game, gameSession);
        gameSession.start();
        gameRepo.save(game);
    }

//    public void sendNextQuestion(Game game, int peerId) {
//        int questionIterator = game.getQuestionIterator();
//        if (questionIterator >= game.getMaxQuestion()) {
//            stopGame(game, peerId);
//            return;
//        }
//        game.setQuestionIterator(++questionIterator);
//        Question question = questionRepo.getRandomQuestion();
//        game.setCurrentQuestion(question);
//        messageSender.sendMessage(peerId, String.format(QUESTION, question.getQuestion()));
//        gameRepo.save(game);
//        Thread answerThread = new Thread(() -> {
//            try {
//                Thread.sleep(25000);
//                messageSender.sendMessage(peerId, String.format(NO_ANSWER, question.getAnswer()));
//                sendNextQuestion(game, peerId);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        });
//        answerThreadMap.put(game, answerThread);
//        answerThread.start();
//    }

    public void stopGame(Game game, int peerId) {
        lobbyRepo.clearGameId(game.getId());
        usersRepo.clearUsersFromGame(game.getId());
        gameRepo.deleteById(game.getId());
        messageSender.sendMessage(peerId, "GAME ENDED");
    }

    public void checkAnswer(Game game, long userId, int peerId, String answer) {
        if (game.getCurrentQuestion().getAnswer().equalsIgnoreCase(answer)) {
            gameSessionMap.get(game).interrupt();
            messageSender.sendMessage(peerId, String.format(RIGHT_ANSWER, userId));
            gameSessionMap.get(game).sendNextQuestion();
        } else {
            messageSender.sendMessage(peerId, String.format(WRONG_ANSWER, userId));
        }
    }

}
