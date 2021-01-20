package dev.vk.bot.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConversationInfo {

    private Response response;

    @Data
    public static class Response {

        private int count;
        private Item[] items;

        @Data
        public static class Item {

                private Peer peer;

                @Data
                public static class Peer {
                    private int id;
                    private String type;
                    @JsonProperty("local_id")
                    private int localId;
                }
            }
        }
}
