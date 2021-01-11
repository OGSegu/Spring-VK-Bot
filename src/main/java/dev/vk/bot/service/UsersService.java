package dev.vk.bot.service;

import dev.vk.bot.entities.Users;
import dev.vk.bot.repositories.UsersRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Data
@Service
public class UsersService {

    @Autowired
    UsersRepository usersRepo;

    public boolean userExists(long userId) {
        Optional<Users> user = usersRepo.findById(userId);
        return user.isPresent();
    }

    public void registerUser(long userId) {
        Users user = new Users(userId);
        usersRepo.save(user);
    }
}
