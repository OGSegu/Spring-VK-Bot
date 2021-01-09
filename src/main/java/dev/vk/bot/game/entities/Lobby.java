package dev.vk.bot.game.entities;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@ToString
@Entity
@Table(schema = "public", name = "lobby")
public class Lobby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private int peerId;

    @Column(unique = true)
    private int chatId;

    @Enumerated(EnumType.STRING)
    private State currentState = State.ALIVE;

    @Column(updatable = false)
    LocalDateTime invitedDate;

    public Lobby() {

    }

    public Lobby(int chatId, int peerId) {
        this.chatId = chatId;
        this.peerId = peerId;
        this.invitedDate = LocalDateTime.now();
    }

    enum State {
        ALIVE,
        ENDED;
    }
}
