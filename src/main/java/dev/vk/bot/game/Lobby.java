package dev.vk.bot.game;

import dev.vk.bot.util.Pack;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Lobby {

    private final Pack pack;
    private final int playersAmount;
    private State currentState = State.ALIVE;

    public Lobby(int packId, int playersAmount) {
        this.pack = new Pack();
        this.playersAmount = playersAmount;
    }

    enum State {
        ALIVE,
        ENDED,
    }
}
