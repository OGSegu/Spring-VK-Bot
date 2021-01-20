package dev.vk.bot.controller;

import dev.vk.bot.config.Config;
import dev.vk.bot.config.MessageAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class MessageSender {
    
    @Autowired
    private Config config;

    @Autowired
    private MessageAPI messageAPI;

    @Autowired
    private RestTemplate restTemplate;

    public void sendMessage(int peerId, String msg) {
        String apiRequest = String.format(messageAPI.getSendMessageAPI(),
                peerId,
                ThreadLocalRandom.current().nextInt(),
                msg,
                config.getGroupId(),
                config.getToken(),
                config.getVersion()
        );
        log.info(apiRequest);
        String response = restTemplate.getForObject(apiRequest, String.class);
        log.info("Message sent. Received: " + response);
    }

    @Bean
    public MessageSender getMessageSender() {
        return new MessageSender();
    }
}
