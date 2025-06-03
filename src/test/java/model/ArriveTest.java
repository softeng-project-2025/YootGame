package model;

import exception.InvalidMoveException;
import model.board.Board;
import model.piece.Piece;
import model.player.Player;
import model.position.Position;
import model.strategy.HexPathStrategy;
import model.yut.YutResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ArriveTest.java
 *
 * < 시나리오 목록 >
 *  1) 한 말이 경로의 마지막 칸에 정확히 도착하면 finished 플래그가 true가 된다.
 *  2) 플레이어가 가진 모든 말이 finished == true 가 되면 hasAllPiecesFinished() 가 true를 반환한다.
 *  3) finished 된 말을 Board.movePiece(...) 로 다시 움직이려 하면 InvalidMoveException 이 발생한다.
 *  4) 이동 스텝이 남아 경로를 초과(오버슛)해도 말은 경로 끝 칸에 고정되며 finished == true 가 된다.
 *  5) 그룹화된 말(두 말 이상이 같은 칸에 업혀 있음)이 함께 결승점에 도착하면 그룹원 모두 finished == true 가 된다.
 */
class ArriveTest {

    /*──────────────── 공통 유틸 ────────────────*/
    private static TestBundle newBundle(int pieceCount) {
        Player player   = new Player(1, "Tester", pieceCount);
        HexPathStrategy s = new HexPathStrategy();
        Board board     = new Board(s);
        List<Position> path = s.getPath();
        player.getPieces().forEach(p -> p.setCustomPath(path));
        return new TestBundle(player, board, path);
    }
    private record TestBundle(Player player, Board board, List<Position> path) {}

    /*──────────────── 시나리오 #1 ────────────────*/
    @Test @DisplayName("단일 말이 도착하면 finished 플래그가 true")
    void singlePieceArrives() {
        TestBundle b = newBundle(2);
        Piece piece  = b.player().getPieces().get(0);

        Position last = b.path().get(b.path().size() - 1);
        piece.moveTo(last, 1);

        assertAll(
                () -> assertTrue(piece.isFinished(), "finished 가 true 여야 함"),
                () -> assertEquals(b.path().size() - 1, piece.getPathIndex()),
                () -> assertFalse(b.player().hasAllPiecesFinished(), "다른 말이 남아 있음")
        );
    }

    /*──────────────── 시나리오 #2 ────────────────*/
    @Test @DisplayName("모든 말이 도착하면 hasAllPiecesFinished() == true")
    void allPiecesArrive() {
        TestBundle b   = newBundle(3);
        Position last  = b.path().get(b.path().size() - 1);

        b.player().getPieces().forEach(p -> p.moveTo(last, 1));

        assertTrue(b.player().hasAllPiecesFinished(), "플레이어 승리 조건 충족");
    }

    /*──────────────── 시나리오 #3 ────────────────*/
    @Test @DisplayName("finished 된 말을 다시 움직이면 InvalidMoveException")
    void cannotMoveAfterArrive() {
        TestBundle b = newBundle(2);
        Piece piece  = b.player().getPieces().get(0);
        Position last = b.path().get(b.path().size() - 1);
        piece.moveTo(last, 1);

        assertThrows(
                InvalidMoveException.class,
                () -> b.board().movePiece(piece, YutResult.DO),
                "완주된 말은 이동할 수 없어야 함"
        );
    }

    /*──────────────── 시나리오 #4 ────────────────*/
    @Test @DisplayName("오버슛해도 마지막 칸에 고정·완주 처리")
    void overshootStillFinishes() {
        TestBundle b = newBundle(2);
        Piece piece  = b.player().getPieces().get(0);
        Position last = b.path().get(b.path().size() - 1);

        piece.moveTo(last, 5);    // 과도한 step

        assertAll(
                () -> assertTrue(piece.isFinished()),
                () -> assertEquals(last, piece.getPosition()),
                () -> assertEquals(b.path().size() - 1, piece.getPathIndex())
        );
    }

    /*──────────────── 시나리오 #5 ────────────────*/
    @Test @DisplayName("그룹화된 두 말이 함께 결승점 도착 → 모두 finished")
    void groupedPiecesArriveTogether() {
        TestBundle b = newBundle(2);
        Board board  = b.board();
        Piece leader   = b.player().getPieces().get(0);
        Piece follower = b.player().getPieces().get(1);

        /* ① index 1에서 그룹 형성 */
        board.movePiece(leader,   YutResult.DO);
        board.movePiece(follower, YutResult.DO);

        /* ② 리더·팔로워를 동일 시퀀스로 이동 */
        YutResult[] seq = {YutResult.YUT, YutResult.GEOL, YutResult.YUT};
        for (YutResult y : seq) {
            board.movePiece(leader,   y);
            board.movePiece(follower, y);
        }

        /* ③ 검증 */
        assertTrue(leader.isFinished());
        assertTrue(follower.isFinished());
        assertEquals(leader.getPosition(), follower.getPosition());
        assertTrue(b.player().hasAllPiecesFinished());
    }

}
