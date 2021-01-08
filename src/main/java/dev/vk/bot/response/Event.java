package dev.vk.bot.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Event {
    private String ts;
    private Update[] updates;
}
