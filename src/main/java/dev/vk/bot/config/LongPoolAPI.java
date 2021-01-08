package dev.vk.bot.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "longpool")
@Data
public class LongPoolAPI {

    private String apiRequest;
    private String method;
    private String parameter;
    private String longPoolRequest;

}
