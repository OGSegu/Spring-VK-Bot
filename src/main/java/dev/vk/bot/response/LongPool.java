package dev.vk.bot.response;

import lombok.Data;

@Data
public class LongPool {

    private Response response;

    @Data
    public class Response {
        private String key;
        private String server;
        private String ts;
    }
}
