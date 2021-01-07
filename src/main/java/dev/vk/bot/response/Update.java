package dev.vk.bot.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Update {
    String type;
    Message object;
    @JsonProperty("group_id")
    long groupId;
    @JsonProperty("event_id")
    String eventId;

    @Data
    @ToString
    public class Message {
        int id;
        long date;
        int out;
        @JsonProperty("user_id")
        int userId;
        @JsonProperty("read_state")
        int readState;
        String title;
        String body;
        @JsonProperty("owner_ids")
        int[] ownerIds;

    }
}
