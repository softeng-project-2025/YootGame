package model.manager;

import model.piece.Piece;
import model.player.Player;
import model.position.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CaptureManagerTest {
    private CaptureManager sut;

    @BeforeEach
    void setUp() {
        sut = new CaptureManager();
    }

    @Test
    void handleCaptures_enemyOnSameIndex_isCaptured() {
        Piece mover = mock(Piece.class);
        Player owner = mock(Player.class);
        when(mover.getOwner()).thenReturn(owner);
        Position pos = mock(Position.class);
        when(pos.index()).thenReturn(3);
        when(mover.getPosition()).thenReturn(pos);

        Piece enemy = mock(Piece.class);
        Player other = mock(Player.class);
        when(enemy.getOwner()).thenReturn(other);
        Position pos2 = mock(Position.class);
        when(pos2.index()).thenReturn(3);
        when(enemy.getPosition()).thenReturn(pos2);
        when(enemy.isFinished()).thenReturn(false);

        when(owner.getPieces()).thenReturn(List.of(mover));
        when(other.getPieces()).thenReturn(List.of(enemy));

        Map<Piece, List<Piece>> result = sut.handleCaptures(
                List.of(mover),
                List.of(owner, other)
        );

        assertTrue(result.containsKey(mover));
        assertEquals(1, result.get(mover).size());
        assertSame(enemy, result.get(mover).get(0));
        verify(enemy).resetToStart();
    }

    @Test
    void handleCaptures_noEnemy_sameOwnerNoCapture() {
        Piece mover = mock(Piece.class);
        Player owner = mock(Player.class);
        when(mover.getOwner()).thenReturn(owner);
        Position pos = mock(Position.class);
        when(pos.index()).thenReturn(5);
        when(mover.getPosition()).thenReturn(pos);

        Piece friend = mock(Piece.class);
        when(friend.getOwner()).thenReturn(owner);
        when(friend.getPosition()).thenReturn(pos);
        when(friend.isFinished()).thenReturn(false);

        when(owner.getPieces()).thenReturn(List.of(mover, friend));

        Map<Piece, List<Piece>> result = sut.handleCaptures(
                List.of(mover),
                List.of(owner)
        );

        assertTrue(result.isEmpty());
        verify(friend, never()).resetToStart();
    }
}
