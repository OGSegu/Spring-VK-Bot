package dev.vk.bot.service;

import dev.vk.bot.game.entities.Lobby;
import dev.vk.bot.repositories.LobbyRepository;
import dev.vk.bot.response.LobbyInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Service
public class LobbyService extends VkClient {

    @Autowired
    LobbyRepository lobbyRepository;

    public static final int PEER_NUMBER = 2000000000;

    // Работает только через OAuth
    public LobbyInfo getLobbyInfo(int chatId) {
        String apiRequest = String.format(config.getApiRequest(),
                "messages.getChat",
                "chat_id",
                chatId,
                config.getToken(),
                config.getVersion()
        );
        log.info("API Request = " + apiRequest);
        LobbyInfo lobbyInfo = restTemplate.getForObject(apiRequest, LobbyInfo.class);
        log.info("Lobby Info = " + lobbyInfo);
        return lobbyInfo;
    }

    public void createLobby(int peerId) {
        log.info("Creating lobby");
        int chatId = peerId - PEER_NUMBER;
        Lobby lobby = new Lobby(chatId, peerId);
        lobbyRepository.save(lobby);
        log.info("Lobby was successfully saved");
    }

    @Bean
    public LobbyService getLobbyService() {
        return new LobbyService();
    }
}
