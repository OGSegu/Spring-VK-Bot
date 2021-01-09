package dev.vk.bot.service;

import dev.vk.bot.config.Config;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VkClientTest {

    @Autowired
    Config config;

    @Autowired
    VkClient vkClient;

    @Test
    void getLongPoolServer() {
        assertNotNull(config.getLongPoolServer());
        assertNotNull(config.getKey());
        assertNotNull(config.getTs());
    }
}