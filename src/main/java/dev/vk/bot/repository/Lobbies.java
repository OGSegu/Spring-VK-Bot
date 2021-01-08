package dev.vk.bot.repository;

import dev.vk.bot.game.Lobby;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
public class Lobbies {

    private final List<Lobby> lobbiesArray = new ArrayList<>();

    public void add(Lobby lobby) {
        lobbiesArray.add(lobby);
    }

    public void remove(Lobby lobby) {
        lobbiesArray.remove(lobby);
    }

    @Bean
    public Lobbies getLobbies() {
        return new Lobbies();
    }
}
