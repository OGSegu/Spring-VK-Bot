package dev.vk.bot.repositories;

import dev.vk.bot.game.entities.Lobby;
import org.springframework.data.repository.CrudRepository;

public interface LobbyRepository extends CrudRepository<Lobby, Long> {

}
