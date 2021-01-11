package dev.vk.bot.repositories;

import dev.vk.bot.entities.Lobby;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LobbyRepository extends CrudRepository<Lobby, Long> {
    Lobby findByPeerId(int peerId);
    Lobby findByGameId(Long gameId);
}
