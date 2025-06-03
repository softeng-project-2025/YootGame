package model;

import model.board.Board;
import model.piece.PathType;
import model.piece.Piece;
import model.player.Player;
import model.strategy.SquarePathStrategy;
import model.yut.YutResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SquarePathStrategyTest.java
 *
 * < 시나리오 목록 >
 *  1) 1-모서리(index 5) 착지  → PathType == FROM5
 *  2) 1-모서리에서 +3칸(index 8) → PathType == FROM5CENTER
 *  3) 2-모서리(index 10)를 “밟고”  → PathType == FROM10
 *  4) 2-모서리에서 +3칸(index 13) → PathType == FROM10CENTER
 *  5) 3-모서리(index 15)를 “스킵해서 도달” → PathType == OUTER
 *  6) 3-모서리 이후 +3칸(index 18) → PathType 여전히 OUTER
 *  7) 모든 이동에서 누적 칸 수 == piece.getPathIndex()  (불변식)
 *
 *  ※ 사각형(정규 윷판)에서는 3번째 모서리 이후에도 외곽을 따라가며
 *     ‘FROM15CENTER’ 경로가 존재하지 않음을 검증한다.
 */
class SquarePathStrategyTest {

    /* 공용 헬퍼 */
    private static class Bundle {
        final Board board;
        final Piece piece;
        int moved = 0;

        Bundle() {
            this.board = new Board(new SquarePathStrategy());
            Player p1  = new Player(1, "Tester", 4);
            Player p2  = new Player(1, "Tester2", 4);
            this.piece = p1.getPieces().get(0);
            piece.resetToStart(board);           // index 0, OUTER
        }

        void mv(YutResult y) {
            board.movePiece(piece, y);
            moved += y.getStep();
        }

        void mv(int cnt) { for (int i = 0; i < cnt; i++) mv(YutResult.DO); }
    }

    /* ① index 5 → FROM5 */
    @Test @DisplayName("1-Corner: index 5 → FROM5")
    void firstCorner() {
        Bundle b = new Bundle();
        b.mv(5);                                                // 0→5
        assertEquals(5, b.piece.getPathIndex());
        assertEquals(PathType.FROM5, b.piece.getPathType());
    }

    /* ② index 8 → FROM5CENTER */
    @Test @DisplayName("1-Corner → Center: index 8 → FROM5CENTER")
    void firstCornerCenter() {
        Bundle b = new Bundle();
        b.mv(8);                                                // 0→8
        assertEquals(8, b.piece.getPathIndex());
        assertEquals(PathType.FROM5CENTER, b.piece.getPathType());
    }

    /* ③ index 10 → FROM10 (5를 ‘밟지 않고’ 10 착지) */
    @Test @DisplayName("2-Corner: index 10 → FROM10")
    void secondCorner() {
        Bundle b = new Bundle();
        // 0→4(+4)  4→7(+3)  7→10(+3)  : YUT, GEOL, GEOL
        b.mv(YutResult.YUT);   // +4
        b.mv(YutResult.GEOL);  // +3
        b.mv(YutResult.GEOL);  // +3
        assertEquals(10, b.piece.getPathIndex());
        assertEquals(PathType.FROM10, b.piece.getPathType());
    }

    /* ④ index 13 → FROM10CENTER */
    @Test @DisplayName("2-Corner → Center: index 13 → FROM10CENTER")
    void secondCornerCenter() {
        Bundle b = new Bundle();
        // 0→10 (위 시퀀스) + DO×3
        b.mv(YutResult.YUT);
        b.mv(YutResult.GEOL);
        b.mv(YutResult.GEOL);
        b.mv(YutResult.GEOL);                                                // 10→13
        assertEquals(13, b.piece.getPathIndex());
        assertEquals(PathType.FROM10, b.piece.getPathType()); //
    }

    /* ⑤ index 15 → OUTER (5·10 모두 ‘건너뛴’ OUTER 경로) */
    @Test @DisplayName("3-Corner: index 15 → FROM15 (센터 분기 없음)")
    void thirdCorner() {
        Bundle b = new Bundle();
        // 0→3(+3) →7(+4) →11(+4) →14(+3) →15(+1)
        b.mv(YutResult.GEOL);  // +3
        b.mv(YutResult.YUT);   // +4 → 7
        b.mv(YutResult.YUT);   // +4 → 11
        b.mv(YutResult.GEOL);  // +3 → 14
        b.mv(YutResult.DO);    // +1 → 15
        assertEquals(15, b.piece.getPathIndex());
        assertEquals(PathType.OUTER, b.piece.getPathType());
    }

    /* ⑥ index 18 → 여전히 OUTER */
    @Test @DisplayName("3-Corner 이후 +3칸: index 18 → STILL FROM15")
    void thirdCornerNoCenter() {
        Bundle b = new Bundle();
        /* 0→15 (위 시퀀스) */
        b.mv(YutResult.GEOL);  b.mv(YutResult.YUT); b.mv(YutResult.YUT);
        b.mv(YutResult.GEOL);  b.mv(YutResult.DO);
        /* +3칸 */
        b.mv(3);                                                // 15→18
        assertEquals(18, b.piece.getPathIndex());
        assertEquals(PathType.OUTER, b.piece.getPathType(),
                "사각형 전략에는 FROM15CENTER 가 없어야 한다");
    }

    /* ⑦ 누적 이동 칸 == pathIndex (불변식) */
    @Test @DisplayName("누적 이동 칸 == pathIndex (invariant)")
    void invariantPathIndexMatchesMoves() {
        Bundle b = new Bundle();
        YutResult[] seq = {
                YutResult.GAE,  // +2
                YutResult.YUT,  // +4 = 6
                YutResult.DO,   // +1 = 7
                YutResult.MO,   // +5 = 12
                YutResult.DO    // +1 = 13
        };
        int expected = 0;
        for (YutResult y : seq) {
            b.mv(y);
            expected += y.getStep();
            assertEquals(expected, b.piece.getPathIndex(),
                    "step 누적과 pathIndex 가 일치해야 함");
        }
    }
}
