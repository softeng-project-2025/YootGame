package model.service;

import model.Game;
import model.dto.MoveFailType;
import model.dto.MoveResult;
import model.manager.TurnManager;
import model.piece.DefaultMovablePieceFinder;
import model.piece.MovablePieceFinder;
import model.piece.Piece;
import model.player.Player;
import model.state.*;
import model.turn.TurnResult;
import model.yut.YutResult;
import model.yut.YutThrower;

import java.util.List;
import java.util.stream.Collectors;

// GameService: 한 턴 단위로 윷 던지기, 이동, 캡처, 그룹핑, 상태 전이를 관리합니다.
public class GameService {
    private final Game game;
    private final MovablePieceFinder finder;
    private final StateTransitioner transitioner;

    public GameService(Game game) {
        this(game, new DefaultMovablePieceFinder());
    }

    GameService(Game game, MovablePieceFinder finder) {
        this.game = game;
        this.finder = finder;
        this.transitioner = new StateTransitioner(game);
    }

    // 새로운 턴을 시작할 때 호출합니다.
    public void startTurn() {
        game.startTurn();
    }

    // 랜덤 윷 던지기
    public MoveResult throwYut() {
        return doThrowYut(YutThrower.throwYut());
    }

    // 지정 윷 던지기 (테스트용)
    public MoveResult throwYut(YutResult yut) {
        return doThrowYut(yut);
    }

    private MoveResult doThrowYut(YutResult yut) {
        if (game.isFinished()) {
            return MoveResult.gameOver(yut, safeCurrentPlayer());
        }

        // 2) 상태 검사
        if (!(game.getState() instanceof CanThrowYut throwState)) {
            return MoveResult.fail(yut, MoveFailType.THROW_REQUIRED);
        }

        // 3) 던지기 로직
        MoveResult result = throwState.handleYutThrow(yut);
        transitioner.transition(result);
        return result;
    }

    // 선택한 말에 윷 결과 적용
    public MoveResult selectPiece(Piece piece, YutResult yut) {
        if (game.isFinished()) {
            return MoveResult.gameOver(yut, safeCurrentPlayer());
        }

        // pending 이 있으면 강제 상태 전환
        TurnResult tr = safeTurnResult();
        if (tr != null && tr.hasPending() && !(game.getState() instanceof CanSelectPiece)) {
            game.transitionTo(new SelectingPieceState(game));
        }
        if (!(game.getState() instanceof CanSelectPiece selState)) {
            return MoveResult.fail(yut, MoveFailType.INVALID_SELECTION);
        }

        // 4) 선택 로직
        MoveResult result = selState.handlePieceSelect(piece, yut);
        transitioner.transition(result);
        return result;
    }

    // null-safe currentPlayer 가져오기
    private Player safeCurrentPlayer() {
        TurnManager tm = game.getTurnManager();
        return tm != null ? tm.currentPlayer() : null;
    }

    // mock 환경에서도 NPE 없이 TurnResult 를 얻도록
    private TurnResult safeTurnResult() {
        try {
            return game.getTurnResult();
        } catch (Exception ignored) {
            return null;
        }
    }

    // 현재 선택 가능한 말 목록

    public List<Piece> getSelectablePieces() {
        TurnResult tr = game.getTurnResult();
        if (tr == null || !tr.hasPending()) return List.of();
        Player current = game.getTurnManager().currentPlayer();
        return tr.getPending().stream()
                .flatMap(y -> finder.findMovable(current, y).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    // 현재 게임 인스턴스 반환
    public Game getGame() {
        return game;
    }

    public void restartGame() {
        game.reset();
    }
}