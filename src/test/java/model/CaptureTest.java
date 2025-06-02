package model;// captureTest.java
// All-in-one JUnit 5 test suite for every capture scenario in CaptureManager.

import model.board.Board;
import model.manager.CaptureManager;
import model.piece.Piece;
import model.player.Player;
import model.position.Position;
import model.strategy.HexPathStrategy;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class CaptureTest {

    private Board board;
    private CaptureManager captureManager;
    private Player p1, p2;

    /** 공통 초기화: 각 테스트마다 새 Board·Player·Piece 생성 */
    @BeforeEach
    void setUp() {
        board = new Board(new HexPathStrategy());
        captureManager = new CaptureManager();

        // 두 플레이어(말 2개씩) 준비
        p1 = new Player(1, "RED", 2);
        p2 = new Player(2, "BLUE", 2);

        // 모든 말의 customPath 초기화
        p1.getPieces().forEach(pc -> pc.resetToStart(board));
        p2.getPieces().forEach(pc -> pc.resetToStart(board));
    }

    /* ---------- 헬퍼 ---------- */

    /** 원하는 index 로 말 이동 (경로 계산 생략용) */
    private void movePieceToIndex(Piece piece, int index) {
        Position target = board.getStrategy().getPath()
                .stream()
                .filter(p -> p.index() == index)
                .findFirst()
                .orElseThrow();
        piece.moveTo(target, 0);          // step=0 → pathIndex 는 내부 로직에서 자동 계산
    }

    /** 캡처 호출 후 매핑 반환 */
    private Map<Piece, List<Piece>> runCapture(Piece... movers) {
        return captureManager.handleCaptures(
                List.of(movers),
                List.of(p1, p2),
                board
        );
    }

    /* ---------- 시나리오별 테스트 ---------- */

    @Test @DisplayName("빈 칸에 착지하면 캡처 없음")
    void noCaptureWhenLandingOnEmptySpot() {
        Piece red = p1.getPieces().get(0);
        movePieceToIndex(red, 5);

        Map<Piece, List<Piece>> cap = runCapture(red);

        assertTrue(cap.isEmpty(), "empty capture map expected");
    }

    @Test @DisplayName("상대 말 1개 캡처")
    void singleCapture() {
        Piece red  = p1.getPieces().get(0);
        Piece blue = p2.getPieces().get(0);

        movePieceToIndex(blue, 5);  // 미리 자리를 차지
        movePieceToIndex(red , 5);  // 착지 → 캡처 발생

        Map<Piece, List<Piece>> cap = runCapture(red);

        assertEquals(1, cap.size());
        assertEquals(List.of(blue), cap.get(red));
        assertEquals(blue.getStartPosition().index(), blue.getPosition().index(),
                "captured piece must reset to its start");
    }

    @Test @DisplayName("상대 말 2개 동시 캡처")
    void multipleCaptureOnSameSpot() {
        Piece red       = p1.getPieces().get(0);
        Piece blue1     = p2.getPieces().get(0);
        Piece blue2     = p2.getPieces().get(1);

        movePieceToIndex(blue1, 10);
        movePieceToIndex(blue2, 10);
        movePieceToIndex(red  , 10);

        Map<Piece, List<Piece>> cap = runCapture(red);

        assertEquals(2, cap.get(red).size(), "both enemy pieces should be captured");
        cap.get(red).forEach(
                c -> assertEquals(c.getStartPosition().index(), c.getPosition().index())
        );
    }

    @Test @DisplayName("같은 칸에 내 말만 있으면 캡처 없음")
    void noCaptureOfOwnPieces() {
        Piece redMover = p1.getPieces().get(0);
        Piece redIdle  = p1.getPieces().get(1);

        movePieceToIndex(redIdle , 15);
        movePieceToIndex(redMover, 15);

        Map<Piece, List<Piece>> cap = runCapture(redMover);

        assertTrue(cap.isEmpty(), "own pieces must not be captured");
    }

    @Test @DisplayName("두 이동자가 서로 다른 칸에서 각각 캡처")
    void twoMoversCaptureIndependently() {
        Piece red1  = p1.getPieces().get(0);
        Piece red2  = p1.getPieces().get(1);
        Piece blue1 = p2.getPieces().get(0);
        Piece blue2 = p2.getPieces().get(1);

        movePieceToIndex(blue1, 5);
        movePieceToIndex(blue2, 20);

        movePieceToIndex(red1 , 5);
        movePieceToIndex(red2 , 20);

        Map<Piece, List<Piece>> cap = runCapture(red1, red2);

        assertEquals(2, cap.size());
        assertEquals(List.of(blue1), cap.get(red1));
        assertEquals(List.of(blue2), cap.get(red2));
    }
}
