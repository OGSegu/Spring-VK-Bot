package dev.vk.bot.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bot")
@Data
@ToString
public class Config {
    private String apiRequest;
    private String method;
    private String parameter;
    private long groupId;
    private String token;
    private double version;

    private String longPoolRequest;
    private String longPoolServer;
    private String key;
    private String ts;
}
