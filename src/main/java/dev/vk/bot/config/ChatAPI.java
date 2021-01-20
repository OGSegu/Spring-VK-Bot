package dev.vk.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chat")
@Data
public class ChatAPI {
    private String conversationByIdAPI;
    private String removeUserFromChatAPI;
}
