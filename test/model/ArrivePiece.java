package model;

import model.board.Board;
import model.piece.Piece;
import model.player.Player;
import model.position.Position;
import model.strategy.SquarePathStrategy;
import model.yut.YutResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//말이 도착하는 시나리오 테스트 케이스
public class ArrivePiece {
    private Game game;
    private Player p1, p2;
    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board(new SquarePathStrategy());
        p1 = new Player("Player1", 4, board);
        p2 = new Player("Player2", 4, board);
        List<Player> players = Arrays.asList(p1, p2);
        game = new Game(board, players);
    }

    /* 테스트 케이스 1: 사용자의 말 하나가 도착점을 통과함.
     * 사용자의 Piece 수가 하나 줄어드는지
     */
    @Test
    void testSinglePieceArrives() {
        List<Position> path = board.getPathStrategy().getPath();
        Piece piece = p1.getPieces().get(0);
        piece.setPosition(path.get(path.size() - 2));

        game.handleYutThrow(YutResult.GAE);
        game.handlePieceSelect(piece);

        assertTrue(piece.isFinished(), "말이 도착점에 도달하면 isFinished는 true여야 함");

        long remaining = p1.getPieces().stream().filter(p -> !p.isFinished()).count();
        assertEquals(3, remaining, "완주한 말을 제외한 나머지 개수는 3개여야 함");
    }


    /* 테스트 케이스2: 여러 사용자의 말 여러개가 도착점을 통과함
     * 각 사용자의 말 수가 정확하게 줄어드는지
     */
    @Test
    void testMultiplePlayersMultiplePiecesArrive() {
        List<Position> path = board.getPathStrategy().getPath();
        Position almostFinish = path.get(path.size() - 2); // 도착 직전

        // Player 1: 1개 도착
        Piece p1Piece = p1.getPieces().get(0);
        p1Piece.setPosition(almostFinish);
        game.handleYutThrow(YutResult.GAE);
        game.handlePieceSelect(p1Piece);

        // Player 2: 2개 도착
        Piece p2Piece1 = p2.getPieces().get(0);
        Piece p2Piece2 = p2.getPieces().get(1);
        p2Piece1.setPosition(almostFinish);
        p2Piece2.setPosition(almostFinish);
        game.handleYutThrow(YutResult.GAE);
        game.handlePieceSelect(p2Piece1);

        // Player 1 turn 처리
        game.handleYutThrow(YutResult.GAE);
        game.handlePieceSelect(p1.getPieces().get(1));

        game.handleYutThrow(YutResult.GAE);
        game.handlePieceSelect(p2Piece2);

        // 상태 확인
        assertTrue(p1Piece.isFinished(), "Player 1의 첫 번째 말은 도착 완료 상태여야 함");
        assertTrue(p2Piece1.isFinished(), "Player 2의 첫 번째 말은 도착 완료 상태여야 함");
        assertTrue(p2Piece2.isFinished(), "Player 2의 두 번째 말은 도착 완료 상태여야 함");

        assertEquals(3, p1.getPieces().stream().filter(p -> !p.isFinished()).count(),
                "Player 1의 남은 말 수는 3개여야 함");
        assertEquals(2, p2.getPieces().stream().filter(p -> !p.isFinished()).count(),
                "Player 2의 남은 말 수는 2개여야 함");
    }

    /* 테스트 케이스 3: 사용자의 업힌 말이 도착점을 통과함.
     * 사용자의 piece 수가 업힌 말 수만큼 줄어드는지
     */
    @Test
    void testGroupedPiecesArriveTogether() {
        List<Position> path = board.getPathStrategy().getPath();
        Position almostFinish = path.get(path.size() - 2); // 도착 직전
        Position secondLast = path.get(path.size() - 3); // 업히기 위해 한 칸 전

        // 두 말 같은 위치로 이동 → 그룹핑
        Piece p1a = p1.getPieces().get(0);
        Piece p1b = p1.getPieces().get(1);
        p1a.setPosition(secondLast);
        p1b.setPosition(secondLast);

        // 그룹핑 시뮬레이션
        game.handleYutThrow(YutResult.DO); // 한 칸 이동
        game.handlePieceSelect(p1a); // 그룹 형성 및 이동

        // 도착 직전 위치로 이동됨
        assertEquals(almostFinish.getIndex(), p1a.getPosition().getIndex(), "업힌 그룹이 도착 직전까지 이동했어야 함");
        assertEquals(almostFinish.getIndex(), p1b.getPosition().getIndex(), "업힌 그룹이 함께 이동해야 함");

        // 다음 이동으로 도착점 통과
        game.handleYutThrow(YutResult.DO); // 한 칸 이동
        game.handlePieceSelect(p2.getPieces().get(0)); // 그룹 형성 및 이동
        game.handleYutThrow(YutResult.GEOL);
        game.handlePieceSelect(p1a);

        // 그룹 내 모든 말이 도착했는지 확인
        assertTrue(p1a.isFinished(), "첫 번째 업힌 말이 완주 상태여야 함");
        assertFalse(p1.getPieces().get(2).isFinished(),"다른 말은 무관해야 함");
        assertTrue(p1b.isFinished(), "두 번째 업힌 말도 완주 상태여야 함");

        // 남은 말 수 = 전체 말 수 - 도착 말 수
        long remaining = p1.getPieces().stream().filter(p -> !p.isFinished()).count();
        assertEquals(2, remaining, "2개의 말이 완주했으므로 남은 말은 2개여야 함");
    }


}
