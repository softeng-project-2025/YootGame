package model;

import exception.GameInitializationException;
import model.board.Board;
import model.player.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GameAndPlayerValidationTest {

    private Board mockBoard() {
        return mock(Board.class);
    }

    @Test
    void gameConstructor_throwsOnTooFewOrTooManyPlayers() {
        Board board = mockBoard();
        Player p1 = new Player(1, "A", 2);
        Player p2 = new Player(2, "B", 2);
        Player p3 = new Player(3, "C", 2);
        Player p4 = new Player(4, "D", 2);
        Player p5 = new Player(5, "E", 2);

        // 1명은 안 되고
        assertThrows(GameInitializationException.class,
                () -> new Game(board, List.of(p1))
        );

        // 5명도 안 된다
        assertThrows(GameInitializationException.class,
                () -> new Game(board, List.of(p1, p2, p3, p4, p5))
        );

        // 2명 또는 4명은 정상
        assertDoesNotThrow(() -> new Game(board, List.of(p1, p2)));
        assertDoesNotThrow(() -> new Game(board, List.of(p1, p2, p3, p4)));
    }

    @Test
    void playerConstructor_throwsOnTooFewOrTooManyPieces() {
        // 말이 1개면 안 되고
        assertThrows(IllegalArgumentException.class,
                () -> new Player(1, "Solo", 1)
        );
        // 말이 6개도 안 된다
        assertThrows(IllegalArgumentException.class,
                () -> new Player(2, "Many", 6)
        );

        // 2개 또는 5개는 정상
        assertDoesNotThrow(() -> new Player(3, "Pair", 2));
        assertDoesNotThrow(() -> new Player(4, "Max", 5));
    }

}