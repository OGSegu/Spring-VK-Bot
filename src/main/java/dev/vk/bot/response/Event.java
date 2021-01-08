package dev.vk.bot.response;

import lombok.Data;

@Data
public class Event {
    private String ts;
    private Update[] updates;
}
