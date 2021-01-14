package dev.vk.bot.repositories;

import dev.vk.bot.entities.Lobby;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface LobbyRepository extends CrudRepository<Lobby, Long> {
    Lobby findByPeerId(int peerId);
    Lobby findByGameId(Long gameId);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE lobby set game_id = NULL")
    void clearGameId();

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE lobby set game_id = NULL WHERE game_id = :id")
    void clearGameId(@Param("id") long gameId);
}
