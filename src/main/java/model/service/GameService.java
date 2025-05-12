package model.service;

import model.Game;
import model.dto.MoveFailType;
import model.dto.MoveResult;
import model.dto.NextStateHint;
import model.manager.CaptureManager;
import model.manager.GroupManager;
import model.piece.DefaultMovablePieceFinder;
import model.piece.MovablePieceFinder;
import model.piece.Piece;
import model.piece.PieceUtil;
import model.player.Player;
import model.state.CanSelectPiece;
import model.state.CanThrowYut;
import model.state.GameOverState;
import model.state.WaitingForThrowState;
import model.yut.YutResult;
import model.yut.YutThrower;

import java.util.List;

// GameService: 한 턴 단위로 윷 던지기, 이동, 캡처, 그룹핑, 상태 전이를 관리합니다.
public class GameService {
    private final Game game;
    private final MovablePieceFinder finder;


    public GameService(Game game) {
        this(game, new DefaultMovablePieceFinder());
    }

    GameService(Game game, MovablePieceFinder finder) {
        this.game = game;
        this.finder = finder;
    }

    // 새로운 턴을 시작할 때 호출합니다.
    public void startTurn() {
        game.startTurn();
    }

    // 랜덤 윷 던지기 처리
    public MoveResult throwYut() {
        if (game.isFinished()) {
            return MoveResult.gameOver(null, game.getTurnManager().currentPlayer());
        }
        if (!(game.getState() instanceof CanThrowYut throwState)) {
            return MoveResult.fail(null, MoveFailType.THROW_REQUIRED);
        }
        YutResult yut = YutThrower.throwYut();
        MoveResult result = throwState.handleYutThrow(yut);
        applyNextState(result);
        return result;
    }
    // 지정 윷 던지기 처리 (테스트용)
    public MoveResult throwYut(YutResult yut) {
        if (game.isFinished()) {
            return MoveResult.gameOver(yut, game.getTurnManager().currentPlayer());
        }
        if (!(game.getState() instanceof CanThrowYut throwState)) {
            return MoveResult.fail(yut, MoveFailType.THROW_REQUIRED);
        }
        MoveResult result = throwState.handleYutThrow(yut);
        applyNextState(result);
        return result;
    }

    // 선택한 말에 윷 결과 적용
    public MoveResult selectPiece(Piece piece, YutResult yut) {
        if (game.isFinished()) {
            return MoveResult.gameOver(yut, game.getTurnManager().currentPlayer());
        }
        if (!(game.getState() instanceof CanSelectPiece selState)) {
            return MoveResult.fail(yut, MoveFailType.INVALID_SELECTION);
        }
        MoveResult result = selState.handlePieceSelect(piece, yut);
        // 이동 후 캡처/그룹핑을 추가로 처리하고 결과에 반영하려면,
        // CaptureManager, GroupManager 호출을 여기에 삽입할 수 있습니다.
        applyNextState(result);
        return result;
    }

    // 상태 전이 및 턴 전환 로직
    private void applyNextState(MoveResult result) {
        if (result == null || result.isFailure()) return;
        NextStateHint hint = result.nextStateHint();
        if (hint == null) return;
        switch (hint) {
            case WAITING_FOR_THROW ->
                    game.transitionTo(new WaitingForThrowState(game));
            case NEXT_TURN -> {
                game.startTurn();
                game.getTurnManager().nextTurn();
                game.transitionTo(new WaitingForThrowState(game));
            }
            case GAME_ENDED ->
                    game.transitionTo(new GameOverState());
            case STAY -> {
                // 현 상태 유지
            }
        }
    }

    // 현재 선택 가능한 말 목록 반환
    public List<Piece> getSelectablePieces() {
        if (!game.getTurnResult().hasPending()){
            return List.of();
        }
        Player current = game.getTurnManager().currentPlayer();
        YutResult last = game.getTurnResult().getLastResult();
        return finder.findMovable(current, last);
    }

    // 현재 게임 인스턴스 반환
    public Game getGame() {
        return game;
    }
}