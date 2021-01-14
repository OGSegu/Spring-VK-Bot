package dev.vk.bot.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class Users {

    @Id
    @Column(unique = true)
    private Long userId;

    private String name;

    private String surname;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private int elo = 0;

    @ManyToOne
    private Game currentGame = null;

    public Users(long userId) {
        this.userId = userId;
    }

    enum Role {
        ADMIN,
        USER
    }
}
