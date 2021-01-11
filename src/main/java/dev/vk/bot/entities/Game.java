package dev.vk.bot.entities;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Data
@Slf4j
@Entity
@Table(schema = "public", name = "game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private State state = State.PREPARING;

    @Column(name = "players_amount")
    private int currentPlayersAmount;


    @OneToOne
    private Lobby lobby;

    @Transient
    private int minPlayersAmount;


    public Game(int playersAmount, Lobby lobby) {
        this.minPlayersAmount = playersAmount;
        this.lobby = lobby;
    }

    public Game() {

    }

    enum State {
        PREPARING,
        WAIT_QUESTION,
        WAIT_ANSWER,
    }
}
