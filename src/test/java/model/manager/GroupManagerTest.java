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

class GroupManagerTest {
    private GroupManager sut;

    @BeforeEach
    void setUp() {
        sut = new GroupManager();
    }

    @Test
    void computeGroups_groupsByOwnerAndPosition() {
        Player p = mock(Player.class);
        Piece a = mock(Piece.class), b = mock(Piece.class);
        Position pos = mock(Position.class);
        when(pos.index()).thenReturn(2);

        when(a.getOwner()).thenReturn(p);
        when(a.getPosition()).thenReturn(pos);
        when(a.isFinished()).thenReturn(false);

        when(b.getOwner()).thenReturn(p);
        when(b.getPosition()).thenReturn(pos);
        when(b.isFinished()).thenReturn(false);

        Map<GroupManager.GroupKey, List<Piece>> groups =
                sut.computeGroups(List.of(a, b));

        assertEquals(1, groups.size());
        var key = new GroupManager.GroupKey(p, pos);
        assertTrue(groups.containsKey(key));
        assertTrue(groups.get(key).containsAll(List.of(a, b)));
    }

    @Test
    void computeGroups_excludesFinishedPieces() {
        Player p = mock(Player.class);
        Piece a = mock(Piece.class), b = mock(Piece.class);
        Position pos = mock(Position.class);
        when(pos.index()).thenReturn(1);

        when(a.getOwner()).thenReturn(p);
        when(a.getPosition()).thenReturn(pos);
        when(a.isFinished()).thenReturn(true);

        when(b.getOwner()).thenReturn(p);
        when(b.getPosition()).thenReturn(pos);
        when(b.isFinished()).thenReturn(false);

        Map<GroupManager.GroupKey, List<Piece>> groups =
                sut.computeGroups(List.of(a, b));

        assertEquals(1, groups.size());
        var key = new GroupManager.GroupKey(p, pos);
        assertEquals(1, groups.get(key).size());
        assertSame(b, groups.get(key).get(0));
    }
}
