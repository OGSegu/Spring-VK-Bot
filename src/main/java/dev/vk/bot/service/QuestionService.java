package dev.vk.bot.service;

import dev.vk.bot.entities.Question;
import dev.vk.bot.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class QuestionService {

    private final Random random = new Random();

    @Autowired
    QuestionRepository questionRepo;

    @Bean
    public QuestionService getQuestionService() {
        return new QuestionService();
    }
}
