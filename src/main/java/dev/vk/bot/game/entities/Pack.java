package dev.vk.bot.game.entities;


import dev.vk.bot.game.Question;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Pack {
    private final int id;
    private final String name;
    private final Question[] questionsArray;

    private Pack(int id, String name, Question[] questionsArray) {
        this.id = id;
        this.name = name;
        this.questionsArray = questionsArray;
    }

    public static Pack getPackById(int id) {

        return new Pack(-1, "null", new Question[0]);
    }


}
