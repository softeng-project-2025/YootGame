package model.manager;

import model.player.Player;

import java.util.List;

public class TurnManager {
    private final List<Player> players;
    private int currentPlayerIndex = 0;

    public TurnManager(List<Player> players) {
        this.players = List.copyOf(players);
    }

    public Player currentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
}