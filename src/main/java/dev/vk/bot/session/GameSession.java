package dev.vk.bot.session;

import dev.vk.bot.entities.Game;
import dev.vk.bot.entities.Question;
import dev.vk.bot.entities.Users;
import dev.vk.bot.service.GameService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static dev.vk.bot.service.GameService.NO_ANSWER;
import static dev.vk.bot.service.GameService.QUESTION;

@Slf4j
@Data
public class GameSession extends Thread {

    Map<Users, Integer> scoreMap = new HashMap<>();

    private final GameService gameService;

    private final Game game;
    private final int peerId;
    private final Iterable<Users> usersIterable;

    AtomicInteger questionIterator;

    public GameSession(GameService gameService, Game game, int peerId, Iterable<Users> usersIterable) {
        this.gameService = gameService;
        this.game = game;
        this.peerId = peerId;
        this.usersIterable = usersIterable;
        this.questionIterator = new AtomicInteger(game.getQuestionIterator());
    }

    @Override
    public void run() {
        addUsersInScoreMap(usersIterable);
        int maxQuestionAmount = game.getMaxQuestion();
        while (questionIterator.get() < maxQuestionAmount) {
            log.info("Iterator: " + questionIterator);
            try {
                Thread.sleep(1500);
                sendNextQuestion();
                Thread.sleep(25000);
            } catch (InterruptedException e) {
                continue;
            }
            gameService.getMessageSender().sendMessage(peerId, String.format(NO_ANSWER, game.getCurrentQuestion().getAnswer()));
        }
        gameService.stopGame(game, peerId);
    }

    public void sendNextQuestion() {
        game.setQuestionIterator(questionIterator.incrementAndGet());
        Question question = gameService.getQuestionRepo().getRandomQuestion();
        game.setCurrentQuestion(question);
        gameService.getMessageSender().sendMessage(peerId, String.format(QUESTION, question.getQuestion()));
        gameService.getGameRepo().save(game);
    }

    public void addUsersInScoreMap(Iterable<Users> usersIterable) {
        usersIterable.forEach(user -> scoreMap.put(user, 0));
    }

    public String getResult() {
        StringBuilder sb = new StringBuilder("--- Результаты ---\n");
        int counter = 1;
        Map<Users, Integer> sorted = scoreMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        for (Map.Entry<Users, Integer> result : sorted.entrySet()) {
            sb.append(counter++)
                    .append(". ")
                    .append(result.getKey().getName())
                    .append(" - ")
                    .append(result.getValue())
                    .append(" баллов\n");
        }
        return sb.toString();
    }
}
