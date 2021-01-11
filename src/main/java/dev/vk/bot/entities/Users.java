package dev.vk.bot.entities;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Entity
public class Users {

    @Id
    @Column(unique = true)
    private Long userId;

    private Role role = Role.USER;

    private int ELO;

    @ManyToOne
    private Game currentGame;

    enum Role {
        ADMIN,
        USER
    }
}
