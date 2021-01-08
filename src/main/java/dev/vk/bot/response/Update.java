package dev.vk.bot.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Update {
    private String type;
    @JsonProperty("object")
    private ReceivedObject data;
    @JsonProperty("group_id")
    private long groupId;
    @JsonProperty("event_id")
    private String eventId;

    @Data
    public class ReceivedObject {
        private Message message;
        @JsonProperty("client_info")
        private ClientInfo clientInfo;

        @Data
        public class Message {
            private long date;
            @JsonProperty("from_id")
            private int fromId;
            private int id;
            private int out;
            @JsonProperty("peer_id")
            private int peerId;
            private String text;
            @JsonProperty("conversation_message_id")
            private int convMessageId;
            private Action action;
            @JsonProperty("fwd_messages")
            private String[] fwdMessages;
            private boolean important;
            @JsonProperty("random_id")
            private int randomId;
            private String[] attachments;
            @JsonProperty("is_hidden")
            private boolean isHidden;

            @Data
            public class Action {
                private String type;
                @JsonProperty("member_id")
                private int memberId;
            }
        }

        @Data
        public class ClientInfo {
            @JsonProperty("button_actions")
            private String[] buttonActions;
            private boolean keyboard;
            @JsonProperty("inline_keyboard")
            private boolean inlineKeyboard;
            private boolean carousel;
            @JsonProperty("lang_id")
            private int langId;
        }
    }
}
