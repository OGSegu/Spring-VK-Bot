package dev.vk.bot.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LobbyInfo {

    private Response response;

    @Data
    public class Response {
        private int id;
        private String type;
        private String title;
        @JsonProperty("admin_id")
        private int adminId;
        private int[] users;
    }
}
