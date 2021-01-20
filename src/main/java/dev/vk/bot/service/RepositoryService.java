package dev.vk.bot.service;


import dev.vk.bot.repositories.GameRepository;
import dev.vk.bot.repositories.LobbyRepository;
import dev.vk.bot.repositories.UsersRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Data
@Service
@Slf4j
public class RepositoryService {

    final LobbyRepository lobbyRepo;
    final UsersRepository usersRepo;
    final GameRepository gameRepo;

    public RepositoryService(LobbyRepository lobbyRepo, UsersRepository usersRepo, GameRepository gameRepo) {
        this.lobbyRepo = lobbyRepo;
        this.usersRepo = usersRepo;
        this.gameRepo = gameRepo;
    }

    @PostConstruct
    void init() {
        log.debug("Cleaning database before starting");
        cleanGameDatabase();
    }

    private void cleanGameDatabase() {
        lobbyRepo.clearGameId();
        usersRepo.clearUsersFromGame();
        gameRepo.deleteAll();
    }

}
