package dev.vk.bot.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class Users {

    @Id
    @Column(unique = true)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private int ELO = 0;

    public Users(long userId) {
        this.userId = userId;
    }

    @ManyToOne
    private Game currentGame = null;

    enum Role {
        ADMIN,
        USER
    }
}
