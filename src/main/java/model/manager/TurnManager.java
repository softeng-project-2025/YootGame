package model.manager;

import exception.GameInitializationException;
import model.player.Player;

import java.util.List;

public class TurnManager {
    private final List<Player> players;
    private int currentPlayerIndex = 0;

    public TurnManager(List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new GameInitializationException(
                    "게임을 시작할 수 없습니다: 플레이어 리스트는 비어 있을 수 없습니다."
            );
        }
        this.players = List.copyOf(players);
    }

    public Player currentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Player nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        return currentPlayer();
    }

    public List<Player> getPlayers() {
        return players;
    }
}