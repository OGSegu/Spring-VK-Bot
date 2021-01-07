package dev.vk.bot.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Event {
    String ts;
    Update[] updates;
}
