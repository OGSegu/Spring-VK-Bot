package dev.vk.bot.controller;

import dev.vk.bot.config.Config;
import dev.vk.bot.config.MessageAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Slf4j
@Component
public class MessageSender {

    Random random = new Random();

    @Autowired
    Config config;

    @Autowired
    MessageAPI messageAPI;

    @Autowired
    private RestTemplate restTemplate;

    public void sendMessage(int userId, String msg) {
        String apiRequest = String.format(messageAPI.getSendMessageAPI(),
                userId,
                random.nextInt(),
                msg,
                config.getGroupId(),
                config.getToken(),
                config.getVersion()
        );
        String response = restTemplate.getForObject(apiRequest, String.class);
        log.info("Message sent. Received: " + response);
    }

    @Bean
    public MessageSender getMessageSender() {
        return new MessageSender();
    }
}
