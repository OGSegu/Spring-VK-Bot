package dev.vk.bot.service;

import dev.vk.bot.component.MessageSender;
import dev.vk.bot.config.ChatAPI;
import dev.vk.bot.config.Config;
import dev.vk.bot.entities.Lobby;
import dev.vk.bot.exception.CannotGetConversation;
import dev.vk.bot.repositories.LobbyRepository;
import dev.vk.bot.response.ConversationInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
@Data
@Service
@Slf4j
public class LobbyValidator {

    private final LobbyRepository lobbyRepo;
    private final RestTemplate restTemplate;
    private final Config mainConfig;
    private final ChatAPI chatConfig;
    private final MessageSender messageSender;

    public LobbyValidator(LobbyRepository lobbyRepo, @Qualifier("conversation") RestTemplate restTemplate, Config mainConfig, ChatAPI chatConfig, MessageSender messageSender) {
        this.lobbyRepo = lobbyRepo;
        this.restTemplate = restTemplate;
        this.mainConfig = mainConfig;
        this.chatConfig = chatConfig;
        this.messageSender = messageSender;
    }

    public ConversationInfo getConversationInfo(int peerId) {
        log.debug("Trying to get conversations info");
        String apiRequest = String.format(chatConfig.getConversationByIdAPI(),
                peerId,
                mainConfig.getGroupId(),
                mainConfig.getToken(),
                mainConfig.getVersion()
        );
        log.debug("API REQUEST: " + apiRequest);
        ConversationInfo conversationInfo = restTemplate.getForObject(apiRequest, ConversationInfo.class);
        if (conversationInfo == null) {
            try {
                throw new CannotGetConversation();
            } catch (CannotGetConversation e) {
                log.debug("Error getting conversation");
                return new ConversationInfo();
            }
        }
        log.info("Conversations was successfully received : " + conversationInfo.toString());
        return conversationInfo;
    }

    @Scheduled(fixedRate = 60_000)
    public void validateLobbies() {
        log.debug("VALIDATING");
        Iterable<Lobby> lobbyIterable = lobbyRepo.findAll();
        for (Lobby lobby : lobbyIterable) {
            log.debug("Checking: " + lobby.getPeerId());
            ConversationInfo.Response response = getConversationInfo(lobby.getPeerId()).getResponse();
            if (response == null || response.getCount() == 0) {
                messageSender.sendMessage(lobby.getPeerId(), "Бот не обладает правами администратора.\nИсключите и добавьте бота в беседу заново");
                log.debug("Deleting: " + lobby.getPeerId());
                lobbyRepo.delete(lobby);
            }
        }
    }

    public boolean waitForAdmin(int peerId) {
        try {
            Thread.sleep(15_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return getConversationInfo(peerId)
                .getResponse()
                .getCount() != 0;
    }

//    private Set<Integer> getAllPeerIds(ConversationInfo conversationInfo) {
//        return Arrays.stream(conversationInfo
//                .getResponse()
//                .getItems())
//                .map(e -> e.getConversation()
//                        .getPeer()
//                        .getId())
//                .collect(Collectors.toSet());
//    }

}
