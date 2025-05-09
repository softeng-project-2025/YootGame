package model.manager;

import model.piece.Piece;
import model.player.Player;

import java.util.List;

public class VictoryManager {
    public static boolean hasPlayerWon(Player player) {
        return player.getPieces().stream().allMatch(Piece::isFinished);
    }

    public static Player findWinner(List<Player> players) {
        return players.stream()
                .filter(VictoryManager::hasPlayerWon)
                .findFirst()
                .orElse(null);
    }
}

