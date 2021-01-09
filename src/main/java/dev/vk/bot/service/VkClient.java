package dev.vk.bot.service;

import dev.vk.bot.config.Config;
import dev.vk.bot.response.LongPool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Data
@Service
@Slf4j
public class VkClient {

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    protected Config config;

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
        LongPool longPool = restTemplate.getForObject(apiRequest, LongPool.class);
        if (longPool == null || longPool.getResponse() == null) {
            log.info("Can't get long pool server");
            System.exit(1);
        }
        log.info("Long pool server was successfully received : " + longPool);
        setLongPoolServer(longPool);
    }

    private void setLongPoolServer(LongPool longPool) {
        String server = longPool.getResponse().getServer();
        String key = longPool.getResponse().getKey();
        String ts = longPool.getResponse().getTs();
        config.setLongPoolServer(server);
        config.setKey(key);
        config.setTs(ts);
    }
}
