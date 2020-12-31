package dev.vk.bot.controller;

import dev.vk.bot.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
@RestController
public class VkController {

    private static final Logger logger = LoggerFactory.getLogger(VkController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Config config;

    @Scheduled(fixedRate = 5000)
    public void sendLongPoolRequest() {
        logger.info("Sent long pool request");
        String apiRequest = String.format(config.getLongPoolRequest(),
                config.getLongPoolServer(),
                config.getKey(),
                config.getTs()
        );
        logger.info("Long pool API request: " + apiRequest);
        String longPoolResponse = restTemplate.getForObject(apiRequest, String.class);
        logger.info("Long pool received: " + longPoolResponse);
    }
}
