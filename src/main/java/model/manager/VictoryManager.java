package model.manager;

import model.player.Player;

import java.util.List;
import java.util.Optional;

public class VictoryManager {
    // 플레이어가 모든 말을 완주했는지 확인합니다.
    public static boolean hasPlayerWon(Player player) {
        return player.hasAllPiecesFinished();

    }

    public static Optional<Player> findWinner(List<Player> players) {
        return players.stream()
                .filter(VictoryManager::hasPlayerWon)
                .findFirst();
    }

    public static boolean hasGameEnded(List<Player> players) {
        return players.stream().anyMatch(VictoryManager::hasPlayerWon);
    }
}

