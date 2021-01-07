package dev.vk.bot.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "message")
@Data
@ToString
public class MessageAPI {

    private String sendMessageAPI;

}
