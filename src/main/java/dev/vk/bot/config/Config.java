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
    String apiRequest;
    String method;
    String parameter;
    long groupId;
    String token;
    double version;
    String longPoolRequest;

    String longPoolServer;
    String key;
    String ts;
}
