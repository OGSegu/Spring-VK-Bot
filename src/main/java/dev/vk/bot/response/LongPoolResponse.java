package dev.vk.bot.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LongPoolResponse {

    private Response response;

    @Data
    @ToString
    public class Response {
        private String key;
        private String server;
        private String ts;
    }
}
