package dev.vk.bot.controller;

import dev.vk.bot.config.Config;
import dev.vk.bot.config.LongPoolAPI;
import dev.vk.bot.response.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
@Slf4j
@Controller
public class VkController {

    private final Config mainConfig;
    private final LongPoolAPI longPoolAPI;
    private final RestTemplate restTemplate;
    private final UpdateParser updateParser;

    public VkController(Config mainConfig, LongPoolAPI longPoolAPI, RestTemplate restTemplate, UpdateParser updateParser) {
        this.mainConfig = mainConfig;
        this.longPoolAPI = longPoolAPI;
        this.restTemplate = restTemplate;
        this.updateParser = updateParser;
    }

    @Scheduled(fixedRate = 100)
    public void sendLongPoolRequest() {
        log.info("Sending long pool request");
        String apiRequest = String.format(longPoolAPI.getLongPoolServerRequest(),
                mainConfig.getLongPoolServer(),
                mainConfig.getKey(),
                mainConfig.getTs()
        );
        log.info(apiRequest);
        Event longPoolResponse = restTemplate.getForObject(apiRequest, Event.class);
        log.info("Long pool received: " + longPoolResponse);
        if (longPoolResponse == null || longPoolResponse.getUpdates() == null) {
            log.warn("Long pool is null");
            return;
        }
        mainConfig.setTs(longPoolResponse.getTs());
        updateParser.parseUpdates(longPoolResponse.getUpdates());
    }
}
