package model;

import model.board.Board;
import model.piece.PathType;
import model.piece.Piece;
import model.player.Player;
import model.strategy.HexPathStrategy;
import model.yut.YutResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HexagonPathStrategyTest.java – 육각형 맵 전략 단위 테스트
 *
 * <시나리오 목록>
 *  1) 1‑모서리(index 5)  → PathType == FROM5
 *  2) 1‑모서리 +3칸(index 8)  → PathType == FROM5CENTER
 *  3) 2‑모서리(index 10)      → PathType == FROM10 (index5 건너뜀 시퀀스)
 *  4) 2‑모서리 +3칸(index 13)  → PathType == FROM10CENTER
 *  5) 3‑모서리(index 15)      → PathType == FROM15
 *  6) 3‑모서리 +3칸(index 18)  → PathType == FROM15CENTER
 *  7) 4‑모서리(index 20)      → PathType == FROM20
 *  8) 4‑모서리 +3칸(index 23)  → PathType == FROM20CENTER
 *  9) 5-Corner: index 25     → PathTupe == OUTER
 *  9) 모든 이동에서 누적 칸 == pathIndex (불변식)
 */
class HexagonPathStrategyTest {

    /* 공용 헬퍼 */
    private static class Bundle {
        final Board board;
        final Piece piece;
        int moved = 0;
        Bundle() {
            board = new Board(new HexPathStrategy());
            Player p1 = new Player(1, "Tester", 4);
            Player p2 = new Player(2, "Tester2", 4);
            piece = p1.getPieces().get(0);
            piece.resetToStart(board);            // index 0, OUTER
        }
        void mv(YutResult y) { board.movePiece(piece, y); moved += y.getStep(); }
        void mv(int cnt) { for (int i = 0; i < cnt; i++) mv(YutResult.DO); }
    }

    /* 1 ─ index 5 → FROM5 */
    @Test @DisplayName("1‑Corner: index 5 → FROM5")
    void firstCorner() {
        Bundle b = new Bundle();
        b.mv(5);
        assertEquals(5, b.piece.getPathIndex());
        assertEquals(PathType.FROM5, b.piece.getPathType());
    }

    /* 2 ─ index 8 → FROM5CENTER */
    @Test @DisplayName("1‑Corner → Center: index 8 → FROM5CENTER")
    void firstCornerCenter() {
        Bundle b = new Bundle();
        b.mv(8);
        assertEquals(8, b.piece.getPathIndex());
        assertEquals(PathType.FROM5CENTER, b.piece.getPathType());
    }

    /* 3 ─ index 10 → FROM10 */
    @Test @DisplayName("2‑Corner: index 10 → FROM10")
    void secondCorner() {
        Bundle b = new Bundle();
        b.mv(YutResult.YUT);   // +4 → 4
        b.mv(YutResult.GEOL);  // +3 → 7
        b.mv(YutResult.GEOL);  // +3 → 10
        assertEquals(10, b.piece.getPathIndex());
        assertEquals(PathType.FROM10, b.piece.getPathType());
    }

    /* 4 ─ index 13 → FROM10CENTER */
    @Test @DisplayName("2‑Corner → Center: index 13 → FROM10CENTER")
    void secondCornerCenter() {
        Bundle b = new Bundle();
        b.mv(YutResult.YUT); b.mv(YutResult.GEOL); b.mv(YutResult.GEOL); // 0→10
        b.mv(3);                                                   // 10→13
        assertEquals(13, b.piece.getPathIndex());
        assertEquals(PathType.FROM10CENTER, b.piece.getPathType());
    }

    /* 5 ─ index 15 → FROM15 */
    @Test @DisplayName("3‑Corner: index 15 → FROM15")
    void thirdCorner() {
        Bundle b = new Bundle();
        b.mv(YutResult.GEOL);  // +3 → 3
        b.mv(YutResult.YUT);   // +4 → 7
        b.mv(YutResult.YUT);   // +4 → 11
        b.mv(YutResult.GEOL);  // +3 → 14
        b.mv(YutResult.DO);    // +1 → 15
        assertEquals(15, b.piece.getPathIndex());
        assertEquals(PathType.FROM15, b.piece.getPathType());
    }

    /* 6 ─ index 18 → FROM15CENTER */
    @Test @DisplayName("3‑Corner → Center: index 18 → FROM15CENTER")
    void thirdCornerCenter() {
        Bundle b = new Bundle();
        // 0→15
        b.mv(YutResult.GEOL); b.mv(YutResult.YUT); b.mv(YutResult.YUT);
        b.mv(YutResult.GEOL); b.mv(YutResult.DO);
        b.mv(3); // 15→18
        assertEquals(18, b.piece.getPathIndex());
        assertEquals(PathType.FROM15CENTER, b.piece.getPathType());
    }

    /* 7 ─ index 20 → FROM20 */
    @Test @DisplayName("4‑Corner: index 20 → FROM20")
    void fourthCorner() {
        Bundle b = new Bundle();
        // 0→20  (YUT×4=16, GEOL=3, DO=1)
        b.mv(YutResult.YUT); b.mv(YutResult.YUT); b.mv(YutResult.YUT); b.mv(YutResult.YUT);
        b.mv(YutResult.GEOL); b.mv(YutResult.DO);
        assertEquals(20, b.piece.getPathIndex());
        assertEquals(PathType.FROM20, b.piece.getPathType());
    }

    /* 8 ─ index 23 → FROM20CENTER */
    @Test @DisplayName("4‑Corner → Center: index 23 → FROM20CENTER")
    void fourthCornerCenter() {
        Bundle b = new Bundle();
        // 0→20
        b.mv(YutResult.YUT); b.mv(YutResult.YUT); b.mv(YutResult.YUT); b.mv(YutResult.YUT);
        b.mv(YutResult.GEOL); b.mv(YutResult.DO);
        b.mv(3); // 20→23
        assertEquals(23, b.piece.getPathIndex());
        assertEquals(PathType.FROM20CENTER, b.piece.getPathType());
    }

    /* 9 index 25 → OUTER (5,10,15,20  모두 ‘건너뛴’ OUTER 경로) */
    @Test @DisplayName("5-Corner: index 25 → OUTER")
    void fifthCorner() {
        HexagonPathStrategyTest.Bundle b = new HexagonPathStrategyTest.Bundle();
        b.mv(YutResult.YUT);
        b.mv(YutResult.MO);
        b.mv(YutResult.MO);
        b.mv(YutResult.MO);
        b.mv(YutResult.MO);
        b.mv(YutResult.DO);
        assertEquals(25, b.piece.getPathIndex());
        assertEquals(PathType.OUTER, b.piece.getPathType());
    }

    /* 10 ─ 불변식 */
    @Test @DisplayName("누적 이동 칸 == pathIndex (invariant)")
    void invariantTotalStepsEqualsIndex() {
        Bundle b = new Bundle();
        int total = 0;
        YutResult[] seq = {YutResult.MO, YutResult.GAE, YutResult.DO, YutResult.YUT};
        for (YutResult y : seq) {
            b.mv(y);
            total += y.getStep();
            assertEquals(total, b.piece.getPathIndex());
        }
    }
}
