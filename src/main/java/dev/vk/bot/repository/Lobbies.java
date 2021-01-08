package dev.vk.bot.repository;

import dev.vk.bot.game.Lobby;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Lobbies {

    private final List<Lobby> lobbiesArray = new ArrayList<>();

    public void add(Lobby lobby) {
        lobbiesArray.add(lobby);
    }

    @Bean
    public Lobbies getLobbies() {
        return new Lobbies();
    }
}
