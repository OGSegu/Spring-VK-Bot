package dev.vk.bot.service;

import dev.vk.bot.config.Config;
import dev.vk.bot.config.LongPoolAPI;
import dev.vk.bot.response.LongPool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Data
@Service
@Slf4j
public class VkClient {

    private final RestTemplate restTemplate;
    private final Config mainConfig;
    private final LongPoolAPI longPoolConfig;
    private final LobbyValidator lobbyValidator;

    public VkClient(RestTemplate restTemplate, Config mainConfig, LongPoolAPI longPoolConfig, LobbyValidator lobbyValidator) {
        this.restTemplate = restTemplate;
        this.mainConfig = mainConfig;
        this.longPoolConfig = longPoolConfig;
        this.lobbyValidator = lobbyValidator;
    }

    @PostConstruct
    void init() {
        getLongPoolServer();
    }

    public void getLongPoolServer() {
        log.debug("Trying to get long pool server");
        String apiRequest = String.format(longPoolConfig.getLongPoolServerGetterAPI(),
                mainConfig.getGroupId(),
                mainConfig.getToken(),
                mainConfig.getVersion()
        );
        log.debug("API REQUEST: " + apiRequest);
        LongPool longPool = restTemplate.getForObject(apiRequest, LongPool.class);
        if (longPool == null || longPool.getResponse() == null) {
            log.debug("Can't get long pool server");
            System.exit(1);
        }
        log.debug("Long pool server was successfully received : " + longPool);
        setLongPoolServer(longPool);
    }

    private void setLongPoolServer(LongPool longPool) {
        String server = longPool.getResponse().getServer();
        String key = longPool.getResponse().getKey();
        String ts = longPool.getResponse().getTs();
        mainConfig.setLongPoolServer(server);
        mainConfig.setKey(key);
        mainConfig.setTs(ts);
    }
}
