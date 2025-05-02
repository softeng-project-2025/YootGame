package model;

import model.board.Board;
import model.piece.Piece;
import model.player.Player;
import model.position.Position;
import model.strategy.SquarePathStrategy;
import model.yut.YutResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//말이 도착하는 시나리오 테스트 케이스
public class ArrivePiece {
    //임의의 새로운 보드를 생성한다. 현재는 사각형 보드만 조재한다.
    private Board createSquareBoard() {
        return new Board(new SquarePathStrategy());
    }


    /* 테스트 케이스 1: 사용자의 말 하나가 도착점을 통과함.
     * 사용자의 Piece 수가 하나 줄어드는지
     */
    @Test
    void testSinglePieceArrives() {
        Board board = createSquareBoard();
        Player player = new Player("Player1", 1, board);
        Piece piece = player.getPieces().get(0);

        // 도착점 바로 전 위치로 이동
        List<Position> path = board.getPathStrategy().getPath();
        piece.setPosition(path.get(path.size() - 2)); // 마지막 전 위치

        // 윷 결과: 한 칸 이동 → 도착점
        YutResult yut = YutResult.GEOL;
        board.movePiece(piece, yut); // 시스템 호출

        // 시스템이 자동으로 도착 처리했는가?
        assertTrue(piece.isFinished());

        long remaining = player.getPieces().stream().filter(p -> !p.isFinished()).count();
        assertEquals(3, remaining);
    }


    /* 테스트 케이스2: 여러 사용자의 말 여러개가 도착점을 통과함
     * 각 사용자의 말 수가 정확하게 줄어드는지
     */
    @Test
    void testMultiplePlayersMultiplePiecesArrive() {
        Board board = createSquareBoard();
        List<Position> path = board.getPathStrategy().getPath();
        Position almostFinish = path.get(path.size() - 2); // 도착 직전
        YutResult oneStep = YutResult.GEOL;

        // Player 1: 4개 중 1개 도착
        Player p1 = new Player("Player1", 4, board);
        Piece p1Piece1 = p1.getPieces().get(0); // 도착할 말
        p1Piece1.setPosition(almostFinish);
        board.movePiece(p1Piece1, oneStep);

        // Player 2: 4개 중 2개 도착
        Player p2 = new Player("Player2", 4, board);
        Piece p2Piece1 = p2.getPieces().get(0);
        Piece p2Piece2 = p2.getPieces().get(1);
        p2Piece1.setPosition(almostFinish);
        p2Piece2.setPosition(almostFinish);
        board.movePiece(p2Piece1, oneStep);
        board.movePiece(p2Piece2, oneStep);

        assertTrue(p1Piece1.isFinished());
        assertTrue(p2Piece1.isFinished());
        assertTrue(p2Piece2.isFinished());
        // 검증
        long remainingP1 = p1.getPieces().stream().filter(p -> !p.isFinished()).count();
        long remainingP2 = p2.getPieces().stream().filter(p -> !p.isFinished()).count();

        assertEquals(3, remainingP1, "Player 1 should have 3 piece remaining.");
        assertEquals(2, remainingP2, "Player 2 should have 2 piece remaining.");
    }

    /* 테스트 케이스 3: 사용자의 업힌 말이 도착점을 통과함.
     * 사용자의 piece 수가 업힌 말 수만큼 줄어드는지
     */


}
