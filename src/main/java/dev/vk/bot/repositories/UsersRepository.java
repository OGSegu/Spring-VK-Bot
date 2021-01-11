package dev.vk.bot.repositories;

import dev.vk.bot.entities.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<Game, Long> {
}
