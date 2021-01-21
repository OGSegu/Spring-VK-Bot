package dev.vk.bot.rating;

import dev.vk.bot.entities.Users;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@Component
public class EloSystem {

    private static int K = 25;

    private double calculateScorePoints(int firstUserElo, int secondUserElo) {
        return 1 / (1 + Math.pow(10, (secondUserElo - firstUserElo) / 400.0));
    }

    public int getEloChange(Users firstUser, Users secondUser, double resultInGame) {
        int firstUserElo = firstUser.getElo();
        int secondUserElo = secondUser.getElo();
        double firstUserScore = calculateScorePoints(firstUserElo, secondUserElo);
        return (int) (K * (resultInGame - firstUserScore));
    }


}


