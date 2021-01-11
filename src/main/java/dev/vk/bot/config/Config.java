package dev.vk.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bot")
@Data
public class Config {

    private long groupId;
    private String token;
    private double version;

    private String longPoolServer;
    private String key;
    private String ts;
}
