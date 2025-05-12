package model.manager;

import exception.GameInitializationException;
import model.player.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TurnManagerTest {

    @Test
    void constructor_nullOrEmpty_throws() {
        assertThrows(GameInitializationException.class, () -> new TurnManager(null));
        assertThrows(GameInitializationException.class, () -> new TurnManager(List.of()));
    }

    @Test
    void currentPlayer_initiallyFirst() {
        Player p1 = new Player(0, "A", 2);
        Player p2 = new Player(1, "B", 2);
        TurnManager tm = new TurnManager(List.of(p1, p2));
        assertSame(p1, tm.currentPlayer());
    }

    @Test
    void nextTurn_cyclesThroughPlayers() {
        Player p1 = new Player(0, "A", 2);
        Player p2 = new Player(1, "B", 2);
        TurnManager tm = new TurnManager(List.of(p1, p2));

        assertSame(p1, tm.currentPlayer());
        assertSame(p2, tm.nextTurn());
        assertSame(p1, tm.nextTurn());
    }

    @Test
    void reset_setsIndexBackToZero() {
        Player p1 = new Player(0, "A", 2);
        Player p2 = new Player(1, "B", 2);
        TurnManager tm = new TurnManager(List.of(p1, p2));
        tm.nextTurn(); // at p2
        tm.reset();
        assertSame(p1, tm.currentPlayer());
    }

    @Test
    void getPlayers_returnsImmutableList() {
        Player p = new Player(0, "X", 2);
        TurnManager tm = new TurnManager(List.of(p));
        var players = tm.getPlayers();
        assertThrows(UnsupportedOperationException.class, () -> players.add(p));
    }
}