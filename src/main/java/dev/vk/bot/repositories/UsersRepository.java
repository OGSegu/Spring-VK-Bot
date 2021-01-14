package dev.vk.bot.repositories;

import dev.vk.bot.entities.Users;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface UsersRepository extends CrudRepository<Users, Long> {

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE users set current_game_id = NULL")
    void clearUsersFromGame();

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE users set current_game_id = NULL where current_game_id = :id")
    void clearUsersFromGame(@Param("id") long gameId);
}
