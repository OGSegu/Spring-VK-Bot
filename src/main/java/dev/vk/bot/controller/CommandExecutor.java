package dev.vk.bot.controller;


import dev.vk.bot.game.Lobby;
import dev.vk.bot.repository.Lobbies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

@Controller
public class CommandExecutor {

    @Autowired
    Lobbies lobbies;

    @Autowired
    MessageSender messageSender;

    void executeOneArgCommand(int peerId, String command) {
        switch (command) {
            case ("Начать"):
                messageSender.sendMessage(peerId, MessageSender.WELCOME);
                break;
            case ("/ping"):
                messageSender.sendMessage(peerId, MessageSender.PONG);
                break;
            case ("/помощь"):
                //messageSender.sendMessage(peerId, "Помощь нужна всем!");
                break;
            default:
                messageSender.sendMessage(peerId, MessageSender.UNKNOWN_COMMAND);
                break;
        }
    }

    void executeMultipleArgsCommand(int peerId, String[] cmdWithArgs) {
        switch (cmdWithArgs[0]) {
            case ("/создать"):
                int packId;
                int playersAmount;
                try {
                    packId = Integer.parseInt(cmdWithArgs[1]);
                    playersAmount = Integer.parseInt(cmdWithArgs[2]);
                } catch (NumberFormatException e) {
                    messageSender.sendMessage(peerId, MessageSender.WRONG_ARGS);
                    return;
                }
                messageSender.sendMessage(peerId, String.format(MessageSender.LOBBY_INFO, packId, playersAmount));
                lobbies.add(new Lobby(packId, playersAmount));
                break;
            case ("/паки"):
                messageSender.sendMessage(peerId, "Паки: ");
                break;
        }
    }

    void executeAction(int peerId, String actionType) {
        switch (actionType) {
            case ("chat_invite_user"):
                messageSender.sendMessage(peerId, MessageSender.WELCOME_IN_CHAT);
                break;
        }
    }

    @Bean
    public CommandExecutor getCommandExecutor() {
        return new CommandExecutor();
    }
}
