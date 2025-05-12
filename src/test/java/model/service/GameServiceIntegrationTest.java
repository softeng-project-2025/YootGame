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
        piece.setCustomPath(board.getStrategy().getPath());
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
        // 경로 초기화
        piece.setCustomPath(board.getStrategy().getPath());
        var path = board.getStrategy().getPath();
        int finalIdx = path.size() - 1;
        int beforeFinal = finalIdx - 1;
        // 마지막 전 칸으로 이동
        piece.moveTo(path.get(beforeFinal), path.get(beforeFinal).index() - piece.getPosition().index());
        assertFalse(piece.isFinished());

        // pending 결과로 '도' 추가
        TurnResult tr = game.getTurnResult();
        tr.add(YutResult.DO);

        // 윷 던지기 -> 말 선택(selectPiece)으로 완전한 턴 로직 수행
        service.throwYut(YutResult.DO);      // state: CanSelectPiece 로 전환
        service.selectPiece(piece, YutResult.DO); // 실제 이동 & applyNextState()

        // 골인 되어야 finished=true 이고, 그 후 다음 턴으로 넘어갑니다
        assertTrue(piece.isFinished());
        assertSame(p2, game.getTurnManager().currentPlayer());
    }

    /**
     * 1) '빽도' 로직: 지나온 길로 한 칸 뒤로 이동하고,
     *    모서리에서 외곽 경로로 돌아오는지 검증합니다.
     */
    @Test
    void backDo_returnsAlongPreviousPath_andCornerResetsToOuter() {
        Board board = game.getBoard();
        Piece piece = p1.getPieces().get(0);

        // customPath 초기화 (outerPath)
        piece.setCustomPath(board.getStrategy().getPath());

        // 1) 두 칸 전진해 경로상 index=2 지점으로 이동
        board.movePiece(piece, YutResult.GAE);   // step=2
        int idxAfter = piece.getPathIndex();
        assertEquals(2, idxAfter);

        // 2) 또 GAE 하면 index=4, 그 후 모서리(5) 직전 상태
        board.movePiece(piece, YutResult.GAE);
        assertEquals(4, piece.getPathIndex());

        // 3) '빽도'(BACK_DO) 실행: 한 칸 뒤로, index=3
        board.movePiece(piece, YutResult.BACK_DO);
        assertEquals(3, piece.getPathIndex());
        // 여전히 outerPath
        assertEquals(PathType.OUTER, piece.getPathType());

        // 4) corner(모)까지 가서 pathType 전환 테스트
        board.movePiece(piece, YutResult.DO); // +1 -> index=4
        board.movePiece(piece, YutResult.DO); // +1 -> index=5 (corner)
        // 아직 corner 도착만 했기에 OUTER 유지
        assertEquals(PathType.OUTER, piece.getPathType());
        // 다음 이동(개)에서만 FROM5로 변경
        board.movePiece(piece, YutResult.GAE);
        assertEquals(PathType.FROM5, piece.getPathType());
    }

    /**
     * 2) 업기(Stack) & 업힌 말 캡처:
     *    두 말을 같은 칸에 모아서 stack, 잡히면 둘 다 reset 되는지 확인합니다.
     */
    @Test
    void stackingAndCapture_resetsAllStacked() {
        Player targetOwner = new Player(0, "P1", 2);
        List<Piece> targetPieces = targetOwner.getPieces();
        Piece a = targetPieces.get(0);
        Piece b = targetPieces.get(1);
        assertEquals(0, a.getPosition().index());
        assertEquals(0, b.getPosition().index());

        Player attacker = new Player(1, "P2", 1);
        Piece captor = attacker.getPieces().get(0);
        assertEquals(0, captor.getPosition().index());

        var caps = new CaptureManager().handleCaptures(
                List.of(captor),
                List.of(targetOwner, attacker)
        );

        assertTrue(caps.containsKey(captor));
        assertTrue(caps.get(captor).containsAll(List.of(a, b)));
        assertEquals(a.getStartPosition(), a.getPosition());
        assertEquals(b.getStartPosition(), b.getPosition());
    }

    /**
     * 3) 골인(Goal-in) 시 턴 전환 & 게임 종료:
     *    마지막 말까지 골인 시 다음 플레이어가 없어 game.isFinished()==true 가 되는지 확인합니다.
     */
    @Test
    void finishingAllPieces_setsGameFinished() {
        // Arrange: finish both players' pieces by moving to goal index
        Piece p1Piece = p1.getPieces().get(0);
        Piece p2Piece = p2.getPieces().get(0);
        List<model.position.Position> path = game.getBoard().getStrategy().getPath();
        p1Piece.setCustomPath(path);
        p1Piece.moveTo(path.get(path.size() - 1), path.size() - 1);
        game.startTurn();
        game.getTurnManager().nextTurn(); // switch to p2
        p2Piece.setCustomPath(path);
        p2Piece.moveTo(path.get(path.size() - 1), path.size() - 1);

        // Assert: both players' pieces are finished
        assertTrue(p1.hasAllPiecesFinished());
        assertTrue(p2.hasAllPiecesFinished());
    }

    /**
     * 4) PentagonPathStrategy / HexPathStrategy 동작 검증:
     *    오각형·육각형 판에서도 corner 진입→다음 이동에서 커스텀 경로 전환이 잘 되는지.
     */
    @Test
    void pentagonAndHexagon_strategiesSwitchAtCorner() {
        game = new Game(new model.board.Board(new model.strategy.PentagonPathStrategy()), List.of(p1));
        Board pent = game.getBoard();
        Piece pp = p1.getPieces().get(0);
        pp.setCustomPath(pent.getStrategy().getPath());
        pent.movePiece(pp, YutResult.MO);
        assertEquals(PathType.OUTER, pp.getPathType());
        pent.movePiece(pp, YutResult.GAE);
        assertEquals(PathType.FROM5, pp.getPathType());

        game = new Game(new model.board.Board(new model.strategy.HexPathStrategy()), List.of(p1));
        Board hex = game.getBoard();
        Piece hh = p1.getPieces().get(0);
        hh.setCustomPath(hex.getStrategy().getPath());
        hex.movePiece(hh, YutResult.MO);
        assertEquals(PathType.OUTER, hh.getPathType());
        hex.movePiece(hh, YutResult.GAE);
        assertEquals(PathType.FROM5, hh.getPathType());
    }



}
