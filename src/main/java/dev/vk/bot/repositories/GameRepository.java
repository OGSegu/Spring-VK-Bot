package dev.vk.bot.repositories;

import dev.vk.bot.entities.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Long> {
}
