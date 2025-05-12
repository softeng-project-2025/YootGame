package model.turn;

import model.piece.Piece;
import model.yut.YutResult;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TurnResultTest {

    @Test
    void addAndHasPending() {
        TurnResult tr = new TurnResult();
        assertFalse(tr.hasPending());

        tr.add(YutResult.DO);
        assertTrue(tr.hasPending());
        assertEquals(YutResult.DO, tr.getLastResult());
    }

    @Test
    void addAllFiltersNull() {
        TurnResult tr = new TurnResult();
        // Arrays.asList는 null 요소를 허용합니다
        List<YutResult> inputs = Arrays.asList(YutResult.YUT, null, YutResult.MO);
        tr.addAll(inputs);
        assertEquals(2, tr.getPending().size());
        assertEquals(YutResult.MO, tr.getPending().get(1));
    }

    @Test
    void applyMovesFromPendingToApplied() {
        TurnResult tr = new TurnResult();
        tr.add(YutResult.MO);
        Piece piece = mock(Piece.class);

        tr.apply(YutResult.MO, piece);

        assertFalse(tr.hasPending());
        assertEquals(1, tr.getApplied().size());
        var applied = tr.getApplied().get(0);
        assertEquals(YutResult.MO, applied.result());
        assertEquals(piece, applied.piece());
    }

    @Test
    void clearEmptiesBoth() {
        TurnResult tr = new TurnResult();
        tr.add(YutResult.DO);
        tr.apply(YutResult.DO, mock(Piece.class));
        tr.clear();
        assertFalse(tr.hasPending());
        assertTrue(tr.getApplied().isEmpty());
    }

    @Test
    void getLastResultEmptyThrows() {
        TurnResult tr = new TurnResult();
        assertThrows(IllegalStateException.class, tr::getLastResult);
    }

    @Test
    void appliedResultConstructorNullThrows() {
        assertThrows(NullPointerException.class,
                () -> new TurnResult.AppliedResult(null, mock(Piece.class)));
        assertThrows(NullPointerException.class,
                () -> new TurnResult.AppliedResult(YutResult.DO, null));
    }
}