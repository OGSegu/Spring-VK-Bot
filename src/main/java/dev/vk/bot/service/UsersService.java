package dev.vk.bot.service;

import dev.vk.bot.config.Config;
import dev.vk.bot.config.UserAPI;
import dev.vk.bot.entities.Users;
import dev.vk.bot.repositories.UsersRepository;
import dev.vk.bot.response.UserInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Data
@Service
@Slf4j
public class UsersService {

    private final UsersRepository usersRepo;
    private final RestTemplate restTemplate;
    private final Config config;
    private final UserAPI userAPI;

    public UsersService(UsersRepository usersRepo, RestTemplate restTemplate, Config config, UserAPI userAPI) {
        this.usersRepo = usersRepo;
        this.restTemplate = restTemplate;
        this.config = config;
        this.userAPI = userAPI;
    }

    public boolean userExists(long userId) {
        Optional<Users> user = usersRepo.findById(userId);
        return user.isPresent();
    }

    public Users getUserFromOptional(Optional<Users> userOptional, long userId) {
        Users user;
        if (userOptional.isEmpty()) {
            user = registerUser(userId);
        } else {
            user = userOptional.get();
        }
        return user;
    }

    public Users registerUser(long userId) {
        Users user = new Users(userId);
        UserInfo userInfo = getUserInfo(userId);
        UserInfo.Response body = userInfo.getResponse()[0];
        user.setName(body.getFirstName());
        user.setSurname(body.getLastName());
        usersRepo.save(user);
        return user;
    }

    public UserInfo getUserInfo(long userId) {
        String request = String.format(userAPI.getUserInfoAPI(),
                userId,
                config.getToken(),
                config.getVersion()
        );
        log.info("USER API: " + request);
        UserInfo userInfo = restTemplate.getForObject(request, UserInfo.class);
        log.info("Received userInfo: " + userInfo);
        return userInfo;
    }

    public String getUserInfoText(long userId) {
        Optional<Users> userOptional = usersRepo.findById(userId);
        Users user = getUserFromOptional(userOptional, userId);
        return String.format("Профиль: %n" +
                        "Имя Фамилия: %s %s%n" +
                        "Рейтинг: %d ELO"
                , user.getName(), user.getSurname(), user.getElo());
    }
}
