package dev.vk.bot.controller;

import dev.vk.bot.config.Config;
import dev.vk.bot.response.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
@RestController
@Slf4j
public class VkController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Config config;

    @Autowired
    private CommandParser commandParser;

    @Scheduled(fixedRate = 1000)
    public void sendLongPoolRequest() {
        log.info("Sending long pool request");
        String apiRequest = String.format(config.getLongPoolRequest(),
                config.getLongPoolServer(),
                config.getKey(),
                config.getTs()
        );
        log.info(apiRequest);
        Event longPoolResponse = restTemplate.getForObject(apiRequest, Event.class);
        log.info("Long pool received: " + longPoolResponse);
        if (longPoolResponse == null || longPoolResponse.getUpdates() == null) {
            log.warn("Long pool is null");
            return;
        }
        config.setTs(longPoolResponse.getTs());
        commandParser.parseUpdates(longPoolResponse.getUpdates());
    }
}
