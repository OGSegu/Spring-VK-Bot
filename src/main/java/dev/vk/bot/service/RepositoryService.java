package dev.vk.bot.service;


import dev.vk.bot.repositories.GameRepository;
import dev.vk.bot.repositories.LobbyRepository;
import dev.vk.bot.repositories.UsersRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Data
@Service
@Slf4j
public class RepositoryService {

    @Autowired
    LobbyRepository lobbyRepo;

    @Autowired
    UsersRepository usersRepo;

    @Autowired
    GameRepository gameRepo;

    @PostConstruct
    void init() {
        log.info("Cleaning database before starting");
        cleanGameDatabase();
    }

    private void cleanGameDatabase() {
        lobbyRepo.clearGameId();
        usersRepo.clearUsersFromGame();
        gameRepo.deleteAll();
    }
}
