package model;

import model.board.Board;
import model.piece.PathType;
import model.piece.Piece;
import model.player.Player;
import model.strategy.PentagonPathStrategy;
import model.yut.YutResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PentagonPathStrategyTest.java
 *
 * <시나리오 목록>
 *  1) 1-모서리(index 5) 착지 → PathType == FROM5
 *  2) 1-모서리에서 +3칸(index 8) → PathType == FROM5CENTER
 *  3) 2-모서리(index 10)를 ‘밟지 않고’ 착지 → PathType == FROM10
 *  4) 2-모서리에서 +3칸(index 13) → PathType == FROM10CENTER
 *  5) 3-모서리(index 15)를 ‘밟지 않고’ 착지 → PathType == FROM15
 *  6) 3-모서리 이후 +3칸(index 18) → PathType == FROM15CENTER
 *  7) 센터 경로로 25번 정점(index 25) 도달해도 PathType 은 그대로 FROM15CENTER
 *  8) 모든 이동에서 누적 칸 수 == piece.getPathIndex() (불변식)
 *
 *  ※ PentagonPathStrategy 는 5·10·15 에서만 분기하며,
 *    20·25 구역은 ‘센터-라인’ 경로를 통해 진입한다.:contentReference[oaicite:0]{index=0}
 */
class PentagonPathStrategyTest {

    /* 공용 헬퍼 */
    private static class Bundle {
        final Board board;
        final Piece piece;
        int moved = 0;

        Bundle() {
            board = new Board(new PentagonPathStrategy());
            Player p1 = new Player(1, "Tester", 4);
            Player p2 = new Player(2, "Tester2", 4);
            piece = p1.getPieces().get(0);
            piece.resetToStart(board);           // index 0, OUTER
        }
        void mv(YutResult y) { board.movePiece(piece, y); moved += y.getStep(); }
        void mv(int cnt)     { for (int i = 0; i < cnt; i++) mv(YutResult.DO); }
    }

    /* 1 ─ index 5 → FROM5 */
    @Test @DisplayName("① 1-Corner: index 5 → FROM5")
    void firstCorner() {
        Bundle b = new Bundle();
        b.mv(5);
        assertEquals(5, b.piece.getPathIndex());
        assertEquals(PathType.FROM5, b.piece.getPathType());     // :contentReference[oaicite:1]{index=1}
    }

    /* 2 ─ index 8 → FROM5CENTER */
    @Test @DisplayName("② 1-Corner → Center: index 8 → FROM5CENTER")
    void firstCornerCenter() {
        Bundle b = new Bundle();
        b.mv(8);
        assertEquals(8, b.piece.getPathIndex());
        assertEquals(PathType.FROM5CENTER, b.piece.getPathType()); // :contentReference[oaicite:2]{index=2}
    }

    /* 3 ─ index 10 → FROM10  (5를 건너뜀) */
    @Test @DisplayName("③ 2-Corner: index 10 → FROM10")
    void secondCorner() {
        Bundle b = new Bundle();
        b.mv(YutResult.YUT);    // +4 → 4
        b.mv(YutResult.GEOL);   // +3 → 7
        b.mv(YutResult.GEOL);   // +3 → 10
        assertEquals(10, b.piece.getPathIndex());
        assertEquals(PathType.FROM10, b.piece.getPathType());    // :contentReference[oaicite:3]{index=3}
    }

    /* 4 ─ index 13 → FROM10CENTER */
    @Test @DisplayName("④ 2-Corner → Center: index 13 → FROM10CENTER")
    void secondCornerCenter() {
        Bundle b = new Bundle();
        /* 0→10 (위 시퀀스) */
        b.mv(YutResult.YUT); b.mv(YutResult.GEOL); b.mv(YutResult.GEOL);
        b.mv(3);             // 10→13
        assertEquals(13, b.piece.getPathIndex());
        assertEquals(PathType.FROM10CENTER, b.piece.getPathType()); // :contentReference[oaicite:4]{index=4}
    }

    /* 5 ─ index 15 → FROM15  (5·10 모두 건너뜀) */
    @Test @DisplayName("⑤ 3-Corner: index 15 → FROM15")
    void thirdCorner() {
        Bundle b = new Bundle();
        b.mv(YutResult.GEOL);  // +3 → 3
        b.mv(YutResult.YUT);   // +4 → 7
        b.mv(YutResult.YUT);   // +4 → 11
        b.mv(YutResult.GEOL);  // +3 → 14
        b.mv(YutResult.DO);    // +1 → 15
        assertEquals(15, b.piece.getPathIndex());
        assertEquals(PathType.FROM15, b.piece.getPathType());    // :contentReference[oaicite:5]{index=5}
    }

    /* 6 ─ index 18 → FROM15CENTER */
    @Test @DisplayName("⑥ 3-Corner → Center: index 18 → FROM15CENTER")
    void thirdCornerCenter() {
        Bundle b = new Bundle();
        /* 0→15 (위 시퀀스) */
        b.mv(YutResult.GEOL); b.mv(YutResult.YUT); b.mv(YutResult.YUT);
        b.mv(YutResult.GEOL); b.mv(YutResult.DO);
        b.mv(3);              // 15→18
        assertEquals(18, b.piece.getPathIndex());
        assertEquals(PathType.FROM15CENTER, b.piece.getPathType()); // :contentReference[oaicite:6]{index=6}
    }

    /* 7 index 20 → OUTER (5,10,15 모두 ‘건너뛴’ OUTER 경로) */
    @Test @DisplayName("⑤ 3-Corner: index 15 → FROM15 (센터 분기 없음)")
    void fourthCorner() {
        PentagonPathStrategyTest.Bundle b = new PentagonPathStrategyTest.Bundle();
        b.mv(YutResult.GEOL);
        b.mv(YutResult.YUT);
        b.mv(YutResult.YUT);
        b.mv(YutResult.GEOL);
        b.mv(YutResult.MO);
        b.mv(YutResult.DO);
        assertEquals(20, b.piece.getPathIndex());
        assertEquals(PathType.OUTER, b.piece.getPathType());   // :contentReference[oaicite:4]{index=4}
    }

    /* 8 ─ 누적 이동 칸 == pathIndex */
    @Test @DisplayName("⑧ 누적 이동 칸 == pathIndex (invariant)")
    void invariantPathIndexEqualsSteps() {
        Bundle b = new Bundle();
        YutResult[] seq = {YutResult.GAE, YutResult.YUT, YutResult.DO,
                YutResult.MO,  YutResult.DO};
        int sum = 0;
        for (YutResult y : seq) {
            b.mv(y);
            sum += y.getStep();
            assertEquals(sum, b.piece.getPathIndex());
        }
    }
}
