package model;

import model.state.GameState;
import model.state.GameOverState;
import model.state.WaitingForThrowState;
import model.turn.TurnResult;
import model.player.Player;
import model.board.Board;
import model.yut.YutResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GameTest {
    @Test
    void constructor_initializesStateAndTurnResult() {
        Player p1 = new Player(0, "A", 1);
        Player p2 = new Player(1, "B", 1);
        Game game = new Game(mockBoard(), List.of(p1, p2));

        assertTrue(game.getState() instanceof WaitingForThrowState);
        assertNotNull(game.getTurnResult());
        assertFalse(game.getTurnResult().hasPending());
    }

    @Test
    void startTurn_resetsTurnResult() {
        Game game = new Game(mockBoard(), List.of(dummyPlayer()));
        TurnResult first = game.getTurnResult();
        first.add(YutResult.DO);
        assertTrue(first.hasPending());

        game.startTurn();
        TurnResult second = game.getTurnResult();
        assertNotSame(first, second);
        assertFalse(second.hasPending());
    }

    @Test
    void getTurnResult_initializesIfNull() {
        Game game = new Game(mockBoard(), List.of(dummyPlayer()));
        // clear internal turnResult to simulate null
        game.startTurn();
        // must not throw
        assertNotNull(game.getTurnResult());
    }

    @Test
    void isFinished_falseInitially_trueAfterTransition() {
        Game game = new Game(mockBoard(), List.of(dummyPlayer()));

        System.out.println("Initial state: " + game.getState().getClass().getSimpleName());
        System.out.println("isFinished = " + game.isFinished());
        assertFalse(game.isFinished());

        game.transitionTo(new GameOverState());
        assertTrue(game.isFinished());
    }

    @Test
    void transitionTo_callsOnExitAndOnEnter() {
        Board board = mockBoard();
        Player player = dummyPlayer();
        Game game = new Game(board, List.of(player));

        // dummy state to track enter/exit
        DummyState from = new DummyState();
        DummyState to = new DummyState();
        game.transitionTo(from);
        game.transitionTo(to);

        assertTrue(from.exited);
        assertTrue(to.entered);
    }

    // helper methods and dummy classes
    private Board mockBoard() {
        return mock(Board.class);
    }
    private Player dummyPlayer() {
        return new Player(0, "X", 0);
    }
    private static class DummyState implements GameState {
        boolean entered = false;
        boolean exited = false;
        @Override public void onEnter(Game g) { entered = true; }
        @Override public void onExit(Game g) { exited = true; }
    }
}