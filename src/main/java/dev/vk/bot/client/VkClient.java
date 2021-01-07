package dev.vk.bot.client;

import dev.vk.bot.config.Config;
import dev.vk.bot.response.LongPoolResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class VkClient {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    Config config;

    @PostConstruct
    void init() {
        getLongPoolServer();
    }

    public void getLongPoolServer() {
        log.info("Trying to get long pool server");
        String apiRequest = String.format(config.getApiRequest(),
                config.getMethod(),
                config.getParameter(),
                config.getGroupId(),
                config.getToken(),
                config.getVersion()
        );
        LongPoolResponse longPoolResponse = restTemplate.getForObject(apiRequest, LongPoolResponse.class);
        if (longPoolResponse == null || longPoolResponse.getResponse() == null) {
            log.info("Can't get long pool server");
            System.exit(1);
        }
        log.info("Long pool server was successfully received : " + longPoolResponse);
        setLongPoolServer(longPoolResponse);
    }

    private void setLongPoolServer(LongPoolResponse longPoolResponse) {
        String server = longPoolResponse.getResponse().getServer();
        String key = longPoolResponse.getResponse().getKey();
        String ts = longPoolResponse.getResponse().getTs();
        config.setLongPoolServer(server);
        config.setKey(key);
        config.setTs(ts);
    }
}
