package dev.vk.bot.service;

import dev.vk.bot.config.Config;
import dev.vk.bot.config.UserAPI;
import dev.vk.bot.entities.UserInfo;
import dev.vk.bot.entities.Users;
import dev.vk.bot.repositories.UsersRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Data
@Service
@Slf4j
public class UsersService {

    @Autowired
    UsersRepository usersRepo;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    Config config;

    @Autowired
    UserAPI userAPI;

    public boolean userExists(long userId) {
        Optional<Users> user = usersRepo.findById(userId);
        return user.isPresent();
    }

    public Users registerUser(long userId) {
        Users user = new Users(userId);
        UserInfo.Response userInfo = getUserInfo(userId).getResponse();
        user.setName(userInfo.getFirstName());
        user.setSurname(userInfo.getLastName());
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
}
