package model.manager;

import model.manager.TurnManager;
import model.player.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class TurnManagerTest {
    @Test
    void nextTurnCyclesThroughPlayers() {
        List<Player> players = List.of(
                new Player(1, "A", 1),
                new Player(2, "B", 1)
        );
        TurnManager tm = new TurnManager(players);
        assertEquals("A", tm.currentPlayer().getName());
        tm.nextTurn();
        assertEquals("B", tm.currentPlayer().getName());
        tm.nextTurn();
        assertEquals("A", tm.currentPlayer().getName());
    }
}