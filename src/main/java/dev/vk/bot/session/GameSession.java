package dev.vk.bot.session;

import dev.vk.bot.entities.Game;
import dev.vk.bot.entities.Question;
import dev.vk.bot.service.GameService;
import lombok.extern.slf4j.Slf4j;

import static dev.vk.bot.service.GameService.NO_ANSWER;
import static dev.vk.bot.service.GameService.QUESTION;

@Slf4j
public class GameSession extends Thread {

    private final GameService gameService;

    private final Game game;
    private final int peerId;

    int questionIterator;

    public GameSession(GameService gameService, Game game, int peerId) {
        this.gameService = gameService;
        this.game = game;
        this.peerId = peerId;
        this.questionIterator = game.getQuestionIterator();
    }

    @Override
    public void run() {
        int maxQuestionAmount = game.getMaxQuestion();
        while (questionIterator < maxQuestionAmount) {
            try {
                sendNextQuestion();
                Thread.sleep(5000);
                gameService.getMessageSender().sendMessage(peerId, String.format(NO_ANSWER, game.getCurrentQuestion().getAnswer()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        gameService.stopGame(game, peerId);
    }

    public void sendNextQuestion() {
        game.setQuestionIterator(++questionIterator);
        Question question = gameService.getQuestionRepo().getRandomQuestion();
        game.setCurrentQuestion(question);
        gameService.getMessageSender().sendMessage(peerId, String.format(QUESTION, question.getQuestion()));
        gameService.getGameRepo().save(game);
    }
}
