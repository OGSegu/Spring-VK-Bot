package dev.vk.bot.component;

import dev.vk.bot.config.Config;
import dev.vk.bot.config.MessageAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ThreadLocalRandom;

@EnableAsync
@Slf4j
@Component
public class MessageSender {

    @Autowired
    private Config config;

    @Autowired
    private MessageAPI messageAPI;

    @Autowired
    @Qualifier("message")
    private RestTemplate restTemplate;

    @Async
    public void sendMessage(int peerId, String msg) {
        String apiRequest = String.format(messageAPI.getSendMessageAPI(),
                peerId,
                ThreadLocalRandom.current().nextInt(),
                msg,
                config.getGroupId(),
                config.getToken(),
                config.getVersion()
        );
        String response = restTemplate.getForObject(apiRequest, String.class);
        String logMsg = "API request: %s%nMessage was sent. Response: %s";
        log.debug(String.format(logMsg, apiRequest, response));
    }

    @Bean
    public MessageSender getMessageSender() {
        return new MessageSender();
    }
}
