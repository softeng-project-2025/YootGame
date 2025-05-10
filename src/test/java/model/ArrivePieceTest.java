package model;

import model.board.Board;
import model.service.GameService;
import model.piece.Piece;
import model.player.Player;
import model.position.Position;
import model.state.SelectingPieceState;
import model.strategy.SquarePathStrategy;
import model.yut.YutResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//말이 도착하는 시나리오 테스트 케이스
public class ArrivePieceTest {
    private GameService gameService;
    private Player p1, p2;
    private Board  board;
    private List<Position> path;   // 편의를 위해 캐싱
    private Game game;

    @BeforeEach
    void setUp() {
        board = new Board(new SquarePathStrategy());
        path  = board.getStrategy().getPath();

        p1 = new Player("Player‑1", 4, board, 1);
        p2 = new Player("Player‑2", 4, board, 2);

        Game game = new Game(board, Arrays.asList(p1, p2));
    }

    /** 유틸 – 윷을 큐에 적재한 뒤 SelectingPieceState 로 전환해서
     *  곧바로 말을 선택할 수 있도록 해준다.
     */
    private void throwAndSelect(YutResult result, Piece piece) {
        gameService.handleYutThrow(result);                          // 윷 결과를 큐에 적재
        game.transitionTo(new SelectingPieceState(game, result)); // 상태 수동 전환
        gameService.handlePieceSelect(piece);                        // 실제 이동 수행
    }

    /* 테스트 케이스 1: 사용자의 말 하나가 도착점을 통과함.
     * 사용자의 Piece 수가 하나 줄어드는지
     */
    @Test
    void testSinglePieceArrives() {
        Piece piece = p1.getPieces().get(0);

        piece.setCustomPath(path);
        piece.setPosition(path.get(path.size() - 2)); // 도착 1칸 전

        throwAndSelect(YutResult.GAE, piece);

        assertTrue(piece.isFinished(), "말이 도착점에 도달하면 isFinished는 true여야 함");

        long remaining = p1.getPieces().stream().filter(p -> !p.isFinished()).count();
        assertEquals(3, remaining, "완주한 말을 제외한 나머지 개수는 3개여야 함");
    }


    /* 테스트 케이스2: 여러 사용자의 말 여러개가 도착점을 통과함
     * 각 사용자의 말 수가 정확하게 줄어드는지
     */
    @Test
    void testMultiplePlayersMultiplePiecesArrive() {
        Position almostFinish = path.get(path.size() - 2);

        Piece p1Piece = p1.getPieces().get(0);
        p1Piece.setCustomPath(path);
        p1Piece.setPosition(almostFinish);
        throwAndSelect(YutResult.GAE, p1Piece); // finish

        Piece p2Piece1 = p2.getPieces().get(0);
        p2Piece1.setCustomPath(path);
        p2Piece1.setPosition(almostFinish);
        throwAndSelect(YutResult.GAE, p2Piece1); // finish

        throwAndSelect(YutResult.GAE, p1.getPieces().get(1));

        Piece p2Piece2 = p2.getPieces().get(1);
        p2Piece2.setCustomPath(path);
        p2Piece2.setPosition(almostFinish);
        throwAndSelect(YutResult.GAE, p2Piece2); // finish

        // 상태 확인
        assertTrue(p1Piece.isFinished(), "Player 1의 첫 번째 말은 도착 완료 상태여야 함");
        assertTrue(p2Piece1.isFinished(), "Player 2의 첫 번째 말은 도착 완료 상태여야 함");
        assertTrue(p2Piece2.isFinished(), "Player 2의 두 번째 말도 도착 완료 상태여야 함");

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
        Position secondLast     = path.get(path.size() - 3); // 그룹 형성 위치
        Position almostFinished = path.get(path.size() - 2); // 도착 1칸 전

        Piece p1a = p1.getPieces().get(0);
        Piece p1b = p1.getPieces().get(1);

        p1a.setCustomPath(path);
        p1b.setCustomPath(path);
        p1a.setPosition(secondLast);
        p1b.setPosition(almostFinished);

        // 그룹핑 시뮬레이션
        throwAndSelect(YutResult.DO, p1a);
        throwAndSelect(YutResult.DO, p2.getPieces().get(0));

        // 도착 직전 위치로 이동됨
        assertEquals(almostFinished.getIndex(), p1a.getPosition().getIndex(), "p1a 도착 직전까지 이동했어야 함");

        // 다음 이동으로 도착점 통과
        throwAndSelect(YutResult.GAE, p1a);

        // 그룹 내 모든 말이 도착했는지 확인
        assertTrue(p1a.isFinished(), "첫 번째 업힌 말이 완주 상태여야 함");
        assertFalse(p1.getPieces().get(2).isFinished(),"다른 말은 무관해야 함");
        assertTrue(p1b.isFinished(), "두 번째 업힌 말도 완주 상태여야 함");

        // 남은 말 수 = 전체 말 수 - 도착 말 수
        long remaining = p1.getPieces().stream().filter(p -> !p.isFinished()).count();
        assertEquals(2, remaining, "2개의 말이 완주했으므로 남은 말은 2개여야 함");
    }


}
