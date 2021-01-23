package dev.vk.bot.controller;

import dev.vk.bot.config.Config;
import dev.vk.bot.config.LongPoolAPI;
import dev.vk.bot.response.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public VkController(Config mainConfig, LongPoolAPI longPoolAPI, @Qualifier("longPool") RestTemplate restTemplate, UpdateParser updateParser) {
        this.mainConfig = mainConfig;
        this.longPoolAPI = longPoolAPI;
        this.restTemplate = restTemplate;
        this.updateParser = updateParser;
    }

    @Scheduled(fixedDelay = 1000)
    public void sendLongPoolRequest() {
        log.debug("Sending long pool request");
        String apiRequest = String.format(longPoolAPI.getLongPoolServerRequest(),
                mainConfig.getLongPoolServer(),
                mainConfig.getKey(),
                mainConfig.getTs()
        );
        log.debug("Long pool API request: " + apiRequest);
        Event longPoolResponse = restTemplate.getForObject(apiRequest, Event.class);
        log.debug("Long pool received: " + longPoolResponse);
        if (longPoolResponse == null) {
            log.warn("Long pool is null");
            return;
        }
        if (longPoolResponse.getUpdates() == null) {
            log.warn("No update has been received:\n" + longPoolResponse.toString());
            return;
        }
        mainConfig.setTs(longPoolResponse.getTs());
        updateParser.parseUpdates(longPoolResponse.getUpdates());
    }
}
