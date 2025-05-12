package model;

import exception.GameInitializationException;
import model.board.Board;
import model.player.Player;
import model.state.GameOverState;
import model.state.GameState;
import model.state.WaitingForThrowState;
import model.turn.TurnResult;
import model.yut.YutResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GameTest {
    @Test
    void constructor_initializesStateAndTurnResult() {
        Player p1 = new Player(0, "A", 2);
        Player p2 = new Player(1, "B", 2);
        Game game = new Game(mockBoard(), List.of(p1, p2));

        assertTrue(game.getState() instanceof WaitingForThrowState);
        assertNotNull(game.getTurnResult());
        assertFalse(game.getTurnResult().hasPending());
    }

    @Test
    void startTurn_resetsTurnResult() {
        // 두 명 이상의 플레이어로 Game 생성
        var players = List.of(dummyPlayer(), otherPlayer());
        Game game = new Game(mockBoard(), players);
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
        var players = List.of(dummyPlayer(), otherPlayer());
        Game game = new Game(mockBoard(), players);
        // clear internal turnResult to simulate null
        game.startTurn();
        // must not throw
        assertNotNull(game.getTurnResult());
    }

    @Test
    void isFinished_falseInitially_trueAfterTransition() {
        var players = List.of(dummyPlayer(), otherPlayer());
        Game game = new Game(mockBoard(), players);

        assertFalse(game.isFinished());

        game.transitionTo(new GameOverState());
        assertTrue(game.isFinished());
    }

    @Test
    void transitionTo_callsOnExitAndOnEnter() {
        Board board = mockBoard();
        Player player = dummyPlayer();
        var players = List.of(dummyPlayer(), otherPlayer());
        Game game = new Game(mockBoard(), players);

        // dummy state to track enter/exit
        DummyState from = new DummyState();
        DummyState to   = new DummyState();
        game.transitionTo(from);
        game.transitionTo(to);

        assertTrue(from.exited);
        assertTrue(to.entered);
    }


    @Test
    void constructor_invalidPlayerCount_throws() {
        Board board = mockBoard();
        // 0명
        assertThrows(GameInitializationException.class,
                () -> new Game(board, List.of())
        );
        // 1명
        assertThrows(GameInitializationException.class,
                () -> new Game(board, List.of(new Player(0, "Solo", 2)))
        );
        // 5명 (최대 4명 초과)
        List<Player> five = List.of(
                new Player(0,"P1",2),
                new Player(1,"P2",2),
                new Player(2,"P3",2),
                new Player(3,"P4",2),
                new Player(4,"P5",2)
        );
        assertThrows(GameInitializationException.class,
                () -> new Game(board, five)
        );
    }

    @Test
    void constructor_invalidPieceCount_throws() {
        // Player 단계에서 검증되므로 IllegalArgumentException 을 기대합니다.
        assertThrows(IllegalArgumentException.class,
                () -> new Player(0, "Low", 1)
        );
        assertThrows(IllegalArgumentException.class,
                () -> new Player(1, "High", 6)
        );
    }

    // helper methods and dummy classes
    private Board mockBoard() {
        return mock(Board.class);
    }
    private Player dummyPlayer() {
        return new Player(0, "X", 2);
    }
    private Player otherPlayer() {
        return new Player(1, "Y", 2);
    }
    private static class DummyState implements GameState {
        boolean entered = false;
        boolean exited  = false;
        @Override public void onEnter(Game g) { entered = true; }
        @Override public void onExit(Game g)  { exited  = true; }
    }
}