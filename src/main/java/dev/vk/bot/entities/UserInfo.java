package dev.vk.bot.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserInfo {

    private Response response;

    @Data
    public class Response {
        @JsonProperty("first_name")
        private String firstName;
        private long id;
        @JsonProperty("last_name")
        private String lastName;
        @JsonProperty("can_access_closed")
        private boolean canAccessClosed;
        @JsonProperty("is_closed")
        private boolean isClosed;
    }
}
