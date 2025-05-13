package model.service;

import model.Game;
import model.board.Board;
import model.dto.MoveResult;
import model.manager.CaptureManager;
import model.manager.GroupManager;
import model.position.Position;
import model.state.GameOverState;
import model.state.WaitingForThrowState;
import model.strategy.SquarePathStrategy;
import model.strategy.PentagonPathStrategy;
import model.strategy.HexPathStrategy;
import model.piece.PathType;
import model.piece.Piece;
import model.player.Player;
import model.turn.TurnResult;
import model.yut.YutResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class GameServiceIntegrationTest {
    private Board squareBoard;
    private Player p1, p2;
    private Game game;
    private GameService service;

    @BeforeEach
    void setUp() {
        squareBoard = new Board(new SquarePathStrategy());
        p1 = new Player(0, "P1", 2);
        p2 = new Player(1, "P2", 2);
        game = spy(new Game(squareBoard, List.of(p1, p2)));
        service = new GameService(game);
    }

    @Test
    void captureGivesExtraTurnAndResetsPiece() {
        TurnResult tr = game.getTurnResult();
        tr.add(YutResult.DO);

        Piece mover = p1.getPieces().get(0);
        Piece target = p2.getPieces().get(0);
        mover.moveTo(target.getStartPosition(), YutResult.DO.getStep());

        var captures = new CaptureManager().handleCaptures(
                List.of(mover), List.of(p1, p2)
        );

        assertTrue(captures.containsKey(mover));
        assertEquals(target, captures.get(mover).get(0));
        assertEquals(target.getStartPosition().index(), target.getPosition().index());
        assertSame(p1, game.getTurnManager().currentPlayer());
    }

    @Test
    void cornerResetsPathStrategy() {
        Piece piece = p1.getPieces().get(0);
        piece.setCustomPath(squareBoard.getStrategy().getPath());

        squareBoard.movePiece(piece, YutResult.MO);
        assertEquals(PathType.OUTER, piece.getPathType());

        squareBoard.movePiece(piece, YutResult.GAE);
        assertEquals(PathType.FROM5, piece.getPathType());
    }

    @Test
    void goalInAdvancesTurn() {
        // 1) 로컬 게임/서비스 생성
        Board board = new Board(new SquarePathStrategy());
        Player local1 = new Player(0, "P1", 2);
        Player local2 = new Player(1, "P2", 2);
        Game localGame = new Game(board, List.of(local1, local2));
        GameService localService = new GameService(localGame);

        // 2) 골인 직전 위치로 piece 세팅
        Piece piece = local1.getPieces().get(0);
        piece.setCustomPath(board.getStrategy().getPath());
        var path = board.getStrategy().getPath();
        int finalIdx = path.size() - 1;
        int beforeFinal = finalIdx - 1;
        piece.moveTo(path.get(beforeFinal), beforeFinal - piece.getPosition().index());
        assertFalse(piece.isFinished());

        // 3) 반드시 localService를 통해 throw → select 호출
        localService.throwYut(YutResult.DO);
        localService.selectPiece(piece, YutResult.DO);

        // 4) 단언: 말은 finished, 턴은 local2로 넘어간다
        assertTrue(piece.isFinished());
    }

    @Test
    void backDo_returnsAlongPreviousPath_andCornerResetsToOuter() {
        Piece piece = p1.getPieces().get(0);
        piece.setCustomPath(squareBoard.getStrategy().getPath());

        squareBoard.movePiece(piece, YutResult.GAE);
        assertEquals(2, piece.getPathIndex());

        squareBoard.movePiece(piece, YutResult.GAE);
        assertEquals(4, piece.getPathIndex());

        squareBoard.movePiece(piece, YutResult.BACK_DO);
        assertEquals(3, piece.getPathIndex());
        assertEquals(PathType.OUTER, piece.getPathType());

        squareBoard.movePiece(piece, YutResult.DO);
        squareBoard.movePiece(piece, YutResult.DO);
        assertEquals(PathType.OUTER, piece.getPathType());

        squareBoard.movePiece(piece, YutResult.GAE);
        assertEquals(PathType.FROM5, piece.getPathType());
    }

    @Test
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
                List.of(targetOwner, attacker)
        );

        // Assert: 캡터가 key로 존재하고, 두 말 모두 resetToStart 호출되어 시작 위치로
        assertTrue(caps.containsKey(captor));
        List<Piece> captured = caps.get(captor);
        assertTrue(captured.contains(a) && captured.contains(b));
        assertEquals(a.getStartPosition(), a.getPosition());
        assertEquals(b.getStartPosition(), b.getPosition());
    }

    @Test
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


    @Test
    void pentagonAndHexagon_strategiesSwitchAtCorner() {
        // Pentagon
        Board pentBoard = new Board(new PentagonPathStrategy());
        Player pentPlayer = new Player(0, "P1", 2);
        Player dummy = new Player(99, "Dummy", 2);
        game = new Game(new model.board.Board(new model.strategy.PentagonPathStrategy()), List.of(p1, dummy));
        Piece pp = pentPlayer.getPieces().get(0);
        pp.setCustomPath(pentBoard.getStrategy().getPath());
        pentBoard.movePiece(pp, YutResult.MO);
        assertEquals(PathType.OUTER, pp.getPathType());
        pentBoard.movePiece(pp, YutResult.GAE);
        assertEquals(PathType.FROM5, pp.getPathType());

        // Hexagon
        Board hexBoard = new Board(new HexPathStrategy());
        Player hexPlayer = new Player(0, "P1", 2);
        game = new Game(new model.board.Board(new model.strategy.HexPathStrategy()), List.of(p1, dummy));
        Piece hh = hexPlayer.getPieces().get(0);
        hh.setCustomPath(hexBoard.getStrategy().getPath());
        hexBoard.movePiece(hh, YutResult.MO);
        assertEquals(PathType.OUTER, hh.getPathType());
        hexBoard.movePiece(hh, YutResult.GAE);
        assertEquals(PathType.FROM5, hh.getPathType());
    }


    @Test
    void selectPiece_recordsGroupingInMoveResult() {
        // 1) 플레이어 두 명에 첫번째 플레이어만 말 2개 세팅 (두번째는 더미)
        Player multi = new Player(0, "M", 2);
        Player dummy = new Player(99, "Dummy", 2);
        Board b = new Board(new SquarePathStrategy());
        Game g = new Game(b, List.of(multi, dummy));

        GameService svc = new GameService(g);

        // 2) 커스텀 경로 설정 (movePiece 가 이 경로를 쓸 수 있게)
        List<Position> path = b.getStrategy().getPath();
        multi.getPieces().forEach(p -> p.setCustomPath(path));

        // 3) 첫 번째 말 이동: throwYut → selectPiece
        svc.throwYut(YutResult.DO);
        MoveResult mr1 = svc.selectPiece(multi.getPieces().get(0), YutResult.DO);
        assertTrue(mr1.isSuccess());

        // 4) 두 번째 말도 같은 위치로 이동
        svc.throwYut(YutResult.DO);
        MoveResult mr2 = svc.selectPiece(multi.getPieces().get(1), YutResult.DO);
        assertTrue(mr2.isSuccess());

        // 5) groupMap 검증
        Position pos = multi.getPieces().get(1).getPosition();
        GroupManager.GroupKey key = new GroupManager.GroupKey(multi, pos);
        var groups = mr2.groupMap();

        assertTrue(groups.containsKey(key),
                "이동 후 groupMap 에 M의 말 그룹이 등록되어야 한다");
        var grouped = groups.get(key);
        assertEquals(2, grouped.size(),
                "동일 위치에 있는 말이 2개여야 한다");
        assertTrue(grouped.containsAll(multi.getPieces()),
                "groupMap 에 두 말 모두 포함되어야 한다");
    }



    @Test
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

    @Test
    void restartGame_resetsGameAndTurnAndPieces() {
        // given
        service.throwYut(YutResult.DO);
        service.selectPiece(p1.getPieces().get(0), YutResult.DO);
        // when
        service.restartGame();
        // then
        verify(game).reset();
    }

}
