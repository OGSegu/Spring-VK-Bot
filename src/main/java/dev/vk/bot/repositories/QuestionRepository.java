package dev.vk.bot.repositories;

import dev.vk.bot.entities.Question;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM question ORDER BY random() LIMIT 1")
    Question getRandomQuestion();
}
