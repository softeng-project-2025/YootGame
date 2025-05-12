package model.piece;

import model.player.Player;
import model.yut.YutResult;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultMovablePieceFinderTest {
    @Test
    void findMovable_delegatesToPieceUtil() {
        Player player = mock(Player.class);
        YutResult last = YutResult.MO;
        List<Piece> expected = List.of(mock(Piece.class));

        try (MockedStatic<PieceUtil> util = mockStatic(PieceUtil.class)) {
            util.when(() -> PieceUtil.getMovablePieces(player, last)).thenReturn(expected);

            DefaultMovablePieceFinder finder = new DefaultMovablePieceFinder();
            List<Piece> actual = finder.findMovable(player, last);

            assertSame(expected, actual);
        }
    }
}