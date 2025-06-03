package model;

import model.board.Board;
import model.manager.CaptureManager;
import model.position.Position;
import model.service.GameService;
import model.state.GameOverState;
import model.strategy.SquarePathStrategy;
import model.piece.Piece;
import model.player.Player;
import model.yut.YutResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

/**
 * GameServiceIntegrationTest.java
 *
 * < 시나리오 목록 >
 *  1) Grouping 상태와 Grouping 초기화(capture 시)
 *  2) 플레이어의 모든 말이 통과
 *  3) 게임 재시작 - state 초기화
 */
class GameServiceIntegrationTest {
    private Board squareBoard;
    private Player p1, p2;
    private Game game;
    private GameService service;

    @BeforeEach @DisplayName("유효한 게임 설정(플레이어 수)")
    void setUp() {
        squareBoard = new Board(new SquarePathStrategy());
        p1 = new Player(0, "P1", 2);
        p2 = new Player(1, "P2", 2);
        game = spy(new Game(squareBoard, List.of(p1, p2)));
        service = new GameService(game);
    }


    @Test @DisplayName("Grouping 상태와 Grouping 초기화(capture 시)")
    void stackingAndCapture_resetsAllStacked() {
        // Arrange: target owner with two pieces
        Player targetOwner = new Player(0, "P1", 2);
        List<Piece> targetPieces = targetOwner.getPieces();
        Piece a = targetPieces.get(0);
        Piece b = targetPieces.get(1);

        // 같은 경로로 설정하여 pathIndex 동기화
        List<Position> path = squareBoard.getStrategy().getPath();
        a.setCustomPath(path);
        b.setCustomPath(path);

        // 둘 다 같은 위치로 이동해서 스택 상태로 만듭니다
        b.moveTo(a.getPosition(), 0);
        assertEquals(a.getPosition().index(), b.getPosition().index());

        // Attacker 설정
        Player attacker = new Player(1, "P2", 2);
        Piece captor = attacker.getPieces().get(0);
        captor.setCustomPath(path);
        captor.moveTo(a.getPosition(), 0);

        // Act: 캡처 수행
        var caps = new CaptureManager().handleCaptures(
                List.of(captor),
                List.of(targetOwner, attacker),
                squareBoard
        );

        // Assert: 캡터가 key로 존재하고, 두 말 모두 resetToStart 호출되어 시작 위치로
        assertTrue(caps.containsKey(captor));
        List<Piece> captured = caps.get(captor);
        assertTrue(captured.contains(a) && captured.contains(b));
        assertEquals(a.getStartPosition(), a.getPosition());
        assertEquals(b.getStartPosition(), b.getPosition());
    }

    @Test @DisplayName("플레이어의 모든 말이 통과")
    void finishingAllPieces_setsGameFinished() {
        // p1 하나만 골인시킨다
        Piece p1Piece = p1.getPieces().get(0);
        List<Position> path = game.getBoard().getStrategy().getPath();
        p1Piece.setCustomPath(path);
        p1Piece.moveTo(path.get(path.size() - 1), /*step*/ path.size() - 1);

        // 바로 게임 오버
        assertTrue(!game.isFinished());
        assertFalse(game.getState() instanceof GameOverState);

        // p1 하나만 골인시킨다
        Piece p1OtherPiece = p1.getPieces().get(1);
        List<Position> otherPath = game.getBoard().getStrategy().getPath();
        p1OtherPiece.setCustomPath(otherPath);
        p1OtherPiece.moveTo(path.get(otherPath.size() - 1), /*step*/ otherPath.size() - 1);

        // 바로 게임 오버
        assertTrue(game.isFinished());
        assertTrue(game.getState() instanceof GameOverState);
    }


    @Test @DisplayName("게임 재시작 - state 초기화")
    void restartGame_resetsAllState() {
        // 1) 윷 던지기로 pending 생성
        service.throwYut(YutResult.DO);
        assertTrue(game.getTurnResult().hasPending(), "throwYut 후에는 pending이 있어야 합니다");

        // 2) 재시작 API 호출
        service.restartGame();

        // 3) 초기화 확인: pending이 초기화되었는지
        assertFalse(game.getTurnResult().hasPending(), "restart 후에는 pending이 없어야 합니다");
        // 말들도 모두 시작 위치로 돌아갔는지
        for (Player pl : game.getPlayers()) {
            for (Piece pc : pl.getPieces()) {
                assertEquals(pc.getStartPosition(), pc.getPosition());
            }
        }
    }

}
