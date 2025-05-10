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

    // 윷 던지기 처리
    public MoveResult throwYut() {
        if (game.isFinished()) {
            return MoveResult.gameOver(game.getTurnManager().currentPlayer());
        }
        if (!(game.getState() instanceof CanThrowYut throwState)) {
            return MoveResult.fail(MoveFailType.THROW_REQUIRED);
        }
        YutResult yut = YutThrower.throwYut();
        MoveResult result = throwState.handleYutThrow(yut);
        applyNextGameState(result);
        return result;
    }

    // 선택한 말에 윷 결과 적용
    public MoveResult selectPiece(Piece piece, YutResult result) {
        if (game.isFinished()) {
            return MoveResult.gameOver(game.getTurnManager().currentPlayer());
        }
        if (!(game.getState() instanceof CanSelectPiece selectState)) {
            return MoveResult.fail(MoveFailType.INVALID_SELECTION);
        }
        MoveResult moveResult = selectState.handlePieceSelect(piece, result);
        applyNextGameState(moveResult);
        return moveResult;
    }

    // 상태 전이 및 턴 초기화
    private void applyNextGameState(MoveResult result) {
        if (result == null || result.isFailure()) {
            return;
        }
        NextStateHint hint = result.nextStateHint();
        switch (hint) {
            case WAITING_FOR_THROW -> {
                game.transitionTo(new WaitingForThrowState(game));
            }
            case NEXT_TURN -> {
                game.startTurn();
                game.getTurnManager().nextTurn();
                game.transitionTo(new WaitingForThrowState(game));
            }
            case GAME_ENDED -> {
                game.transitionTo(new GameOverState());
            }
            case STAY -> {
                // 현재 상태 유지
            }
        }
    }

    // 현재 선택 가능한 말 목록 반환
    public List<Piece> getSelectablePieces() {
        Player current = game.getTurnManager().currentPlayer();
        YutResult last = game.getTurnResult().getLastResult();
        return PieceUtil.getMovablePieces(current, last);
    }
}