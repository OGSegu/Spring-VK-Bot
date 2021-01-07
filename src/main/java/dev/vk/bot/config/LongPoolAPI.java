package dev.vk.bot.config;


import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "longpool")
@Data
@ToString
public class LongPoolAPI {

    private String apiRequest;
    private String method;
    private String parameter;
    private String longPoolRequest;

}
