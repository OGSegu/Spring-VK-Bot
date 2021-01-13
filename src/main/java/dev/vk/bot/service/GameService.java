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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@Slf4j
@Service
public class GameService {


    /* ANSWER THREAD */
    Map<Game, Thread> answerThreadMap = new HashMap<>();


    /* ERROR */
    private static final String ALREADY_IN_GAME = "Вы уже участвуете в игре";

    /* GAME PREPARATION */
    private static final String GAME_INFO = "------Игра------%nКол-во игроков: %d/%d";
    private static final String GAME_READY = "Все игроки набраны. Игра начинается!";

    /* GAME PROCESS */
    private static final String WELCOME_TO_GAME = "--- Добро пожаловать на викторину ---\nКоманды:\n/o *ответ* - для того чтобы ответить на вопрос";
    private static final String QUESTION = "Вопрос:%n%s";
    private static final String RIGHT_ANSWER = "%d правильно ответил(а) на вопрос. Продолжаем.";
    private static final String WRONG_ANSWER = "%d неверно ответил(а) на вопрос.";
    private static final String NO_ANSWER = "Никому не удалось ответить.%nПравильный ответ: %s";


    @Autowired
    LobbyRepository lobbyRepo;

    @Autowired
    GameRepository gameRepo;

    @Autowired
    UsersRepository usersRepo;

    @Autowired
    QuestionRepository questionRepo;

    @Autowired
    MessageSender messageSender;


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

    Game createGame(int playersAmount, Lobby lobby) {
        Game game = new Game(playersAmount, lobby);
        gameRepo.save(game);
        return game;
    }

    public void addParticipant(int peerId, long userId) {
        Game game = getGame(peerId);
        Optional<Users> user = usersRepo.findById(userId);
        if (user.get().getCurrentGame() != null) {
            messageSender.sendMessage(game.getLobby().getPeerId(), ALREADY_IN_GAME);
            return;
        }
        int currentPlayersAmount = game.getCurrentPlayersAmount();
        game.setCurrentPlayersAmount(++currentPlayersAmount);
        gameRepo.save(game);
        user.get().setCurrentGame(game);
        usersRepo.save(user.get());
        if (isReadyToStart(game)) {
            sendReadyMsg(peerId);
            startGame(game, peerId);
        } else {
            sendStateMsg(game, peerId);
        }
    }

    public void startGame(Game game, int peerId) {
        game.setState(Game.State.STARTING);
        messageSender.sendMessage(peerId, WELCOME_TO_GAME);
        startGameProcess(game, peerId);
    }

    public void startGameProcess(Game game, int peerId) {
        sendNextQuestion(game, peerId);
        gameRepo.save(game);
    }

    public void sendNextQuestion(Game game, int peerId) {
        Question question = questionRepo.getRandomQuestion();
        game.setCurrentQuestion(question);
        messageSender.sendMessage(peerId, String.format(QUESTION, question.getQuestion()));
        gameRepo.save(game);
        Thread answerThread = new Thread(() -> {
            try {
                Thread.sleep(25000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            messageSender.sendMessage(peerId, String.format(NO_ANSWER, question.getAnswer()));
            sendNextQuestion(game, peerId);
        });
        answerThreadMap.put(game, answerThread);
        answerThread.start();
    }

    public void checkAnswer(Game game, long userId, int peerId, String answer) {
        if (game.getCurrentQuestion().getAnswer().equalsIgnoreCase(answer)) {
            messageSender.sendMessage(peerId, String.format(RIGHT_ANSWER, userId));
            answerThreadMap.get(game).interrupt();
            sendNextQuestion(game, peerId);
        } else {
            messageSender.sendMessage(peerId, String.format(WRONG_ANSWER, userId));
        }
    }

}
