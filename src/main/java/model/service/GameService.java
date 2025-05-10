package model.service;

import model.Game;
import model.dto.MoveFailType;
import model.dto.MoveResult;
import model.dto.NextStateHint;
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

// GameService: 한 턴 단위로 게임 흐름(윷 던지기→이동→캡처→그룹→턴 전환→승리 검사)을 관리합니다.
public class GameService {
    private final Game game;

    public GameService(Game initialGame) {
        this.game = initialGame;
    }

    // 1. 턴 시작 시 초기화
    public void startTurn() {
        game.newTurnResult();
    }

    // 2. 윷 던지기: 결과 누적
    public YutResult throwAndUpdate() {
        YutResult result = throwYut();
        game.getTurnResult().add(result);
        return result;
    }

    // 3. 윷 결과를 특정 말에 적용
    public MoveResult applyResultToPiece(YutResult result, Piece piece) {
        if (game.isFinished()) {
            return MoveResult.gameOver(game.getTurnManager().currentPlayer());
        }

        if (!(game.getState() instanceof CanSelectPiece selectState)) {
            return MoveResult.fail(MoveFailType.INVALID_SELECTION);
        }

        MoveResult moveResult = selectState.handlePieceSelect(piece, result);
        game.getTurnResult().apply(result, piece);

        if (moveResult.bonusTurn()) {
            YutResult bonus = throwYut();
            game.getTurnResult().add(bonus);
        }

        applyNextGameState(moveResult);
        return moveResult;
    }

    // 4. 던지기 상태에서 직접 입력 결과 처리 (선택적 지원)
    public MoveResult handleYutThrow(YutResult result) {
        if (game.isFinished()) {
            return MoveResult.gameOver(game.getTurnManager().currentPlayer());
        }

        if (!(game.getState() instanceof CanThrowYut throwState)) {
            return MoveResult.fail(MoveFailType.THROW_REQUIRED);
        }

        MoveResult resultAfterThrow = throwState.handleYutThrow(result);
        applyNextGameState(resultAfterThrow);
        return resultAfterThrow;
    }

    // 5. 상태 전이 처리
    private void applyNextGameState(MoveResult result) {
        if (result == null || result.isFailure()) return;

        if (result.isGameOver()) {
            game.setFinished(true);
            return;
        }

        NextStateHint hint = result.nextStateHint();
        if (hint == null) return;

        switch (hint) {
            case WAITING_FOR_THROW -> game.transitionTo(new WaitingForThrowState(game));
            case NEXT_TURN -> {
                Player player = game.getTurnManager().nextTurn();
                game.transitionTo(new WaitingForThrowState(game));

            }
            case GAME_ENDED -> {
                game.transitionTo(new GameOverState());
            }

            case STAY -> {}
        }
    }

    private YutResult throwYut() {
        return YutThrower.throwYut();
    }


    public List<Piece> getSelectablePieces() {
        return PieceUtil.getMovablePieces(game.getTurnManager().currentPlayer(), game.getTurnResult().getLastResult());
    }
}