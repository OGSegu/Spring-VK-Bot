package dev.vk.bot.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UserInfo {

    private Response[] response;

    @Data
    @NoArgsConstructor
    public static class Response {
        @JsonProperty("first_name")
        private String firstName;
        private long id;
        @JsonProperty("last_name")
        private String lastName;
    }
}
