package dev.vk.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "message")
@Data
public class MessageAPI {
    private String sendMessageAPI;
}
