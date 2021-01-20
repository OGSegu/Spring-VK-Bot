package dev.vk.bot;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateCreator {

    @Bean("longPool")
    public RestTemplate restTemplateConversationLongPooling() {
        return new RestTemplateBuilder().build();
    }

    @Bean("message")
    public RestTemplate restTemplateMessage() {
        return new RestTemplateBuilder().build();
    }

    @Bean("conversation")
    @Primary
    public RestTemplate restTemplateConversation() {
        return new RestTemplateBuilder().build();
    }

}
