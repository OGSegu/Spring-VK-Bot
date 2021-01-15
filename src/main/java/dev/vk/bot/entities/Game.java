package dev.vk.bot.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Data
@EqualsAndHashCode(of = "id")
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

    @ToString.Exclude
    @OneToOne
    private Lobby lobby;

    @Column(name = "players_to_start")
    private int playersToStart;

    @OneToOne
    private Question currentQuestion;

    private int questionIterator = 0;

    @Column(updatable = false)
    private int maxQuestion;

    public Game(int playersAmount, int maxQuestion, Lobby lobby) {
        this.playersToStart = playersAmount;
        this.maxQuestion = maxQuestion;
        this.lobby = lobby;
    }

    public Game() {

    }

    public enum State {
        PREPARING,
        ALIVE,
    }
}
