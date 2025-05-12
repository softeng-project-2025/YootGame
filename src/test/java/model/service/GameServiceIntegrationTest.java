// src/test/java/model/service/GameServiceIntegrationTest.java
package model.service;

import model.Game;
import model.board.Board;
import model.strategy.SquarePathStrategy;
import model.manager.CaptureManager;
import model.piece.PathType;
import model.piece.Piece;
import model.player.Player;
import model.turn.TurnResult;
import model.yut.YutResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceIntegrationTest {
    private Game game;
    private GameService service;
    private Player p1, p2;

    @BeforeEach
    void setUp() {
        Board board = new Board(new SquarePathStrategy());
        p1 = new Player(0, "P1", 1);
        p2 = new Player(1, "P2", 1);
        game = new Game(board, List.of(p1, p2));
        service = new GameService(game);
    }

    // 말을 잡았을 때 CaptureManager를 직접 호출해 캡처가 일어나고, 잡힌 말이 시작 위치로 반환되는지, 그리고 추가 턴이 유지되는지 검증합니다.
    @Test
    void captureGivesExtraTurnAndResetsPiece() {
        // Arrange: p1 has a pending DO
        TurnResult tr = game.getTurnResult();
        tr.add(YutResult.DO);

        Piece mover = p1.getPieces().get(0);
        Piece target = p2.getPieces().get(0);
        // Move mover onto target's start position for capture
        mover.moveTo(target.getStartPosition(), YutResult.DO.getStep());

        // Act: capture
        var captures = new CaptureManager().handleCaptures(
                List.of(mover), List.of(p1, p2)
        );

        // Assert: target captured and reset
        assertTrue(captures.containsKey(mover));
        assertEquals(target, captures.get(mover).get(0));
        assertEquals(target.getStartPosition().index(), target.getPosition().index());
        // p1 retains turn
        assertSame(p1, game.getTurnManager().currentPlayer());
    }

    // ‘모’(index 5)에 도달했을 때 PathType이 FROM5로 변경되는지 확인합니다.
    @Test
    void cornerResetsPathStrategy() {
        // Arrange: initial path remains OUTER after landing on corner via board.movePiece
        Board board = game.getBoard();
        Piece piece = p1.getPieces().get(0);
        // first move: 'MO' to reach index 5 (corner)
        board.movePiece(piece, YutResult.MO);
        // pathType should still be OUTER at arrival
        assertEquals(PathType.OUTER, piece.getPathType());

        // Act: next move 'GAE' should trigger path strategy update
        board.movePiece(piece, YutResult.GAE);

        // Assert: now pathType changes to FROM5
        assertEquals(PathType.FROM5, piece.getPathType());
    }

    // 목표 지점 직전에서 “도”를 사용해 finish시키면 Piece.isFinished()가 true가 되고, GameService.selectPiece 호출 후 턴이 다음 플레이어로 넘어가는지 검사합니다.
    @Test
    void goalInAdvancesTurn() {
        // Arrange: move p1 piece to one step before goal
        Board board = game.getBoard();
        Piece piece = p1.getPieces().get(0);
        piece.setCustomPath(board.getStrategy().getPath());
        var path = board.getStrategy().getPath();
        int finalIdx = path.size() - 1;
        int beforeFinal = finalIdx - 1;
        piece.moveTo(path.get(beforeFinal), path.get(beforeFinal).index() - piece.getPosition().index());
        assertFalse(piece.isFinished());

        // Simulate pending DO
        TurnResult tr = game.getTurnResult();
        tr.add(YutResult.DO);

        // Act: finish move via selectPiece
        service.selectPiece(piece, YutResult.DO);

        // Assert: piece finished and turn advanced
        assertTrue(piece.isFinished());
        assertSame(p2, game.getTurnManager().currentPlayer());
    }
}
