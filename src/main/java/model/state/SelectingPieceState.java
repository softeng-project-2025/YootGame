package model.state;

import model.Game;
import model.dto.*;
import model.manager.VictoryManager;
import model.piece.Piece;
import model.piece.PieceUtil;
import model.turn.TurnResult;
import model.yut.YutResult;

import java.util.List;

public class SelectingPieceState implements CanSelectPiece {

    private final Game game;


    public SelectingPieceState(Game game) {
        this.game = game;
    }

    @Override
    public MoveResult handlePieceSelect(Piece piece, YutResult selectedResult) {
        // 유효성 검사
        if (piece.isFinished() || piece.getOwner() != game.getTurnManager().currentPlayer()) {
            return MoveResult.fail(MoveFailType.INVALID_SELECTION);
        }

        TurnResult turnResult = game.getTurnResult();
        if (!turnResult.getAvailable().contains(selectedResult)) {
            return MoveResult.fail(MoveFailType.NO_RESULT);
        }

        turnResult.apply(selectedResult, piece);

        // 그룹 이동
        List<Piece> group = PieceUtil.getMovableGroup(piece, game);
        boolean captured = game.getBoard().movePiece(piece, selectedResult, game.getPlayers());

        // 골인 체크
        var owner = piece.getOwner();
        if (PieceUtil.allFinished(group, game.getBoard())) {
            group.forEach(p -> {
                p.setFinished(true);
                PieceUtil.resetGroupToSelf(p);
            });
        }

        // 승리 조건 확인
        boolean isGameOver = VictoryManager.hasPlayerWon(owner);

        // 보너스 턴 조건
        boolean isYutOrMo = selectedResult == YutResult.YUT || selectedResult == YutResult.MO;
        boolean bonusTurn = (captured && !isYutOrMo) || (isYutOrMo && !captured);
        boolean hasMoreResults = turnResult.hasPending();

        // 다음 상태 결정
        MoveResult result = MoveResult.success(
                captured,
                bonusTurn,
                isGameOver ? owner : null,
                game,
                piece,
                hasMoreResults
        );

        // 상태 전이 힌트 설정
        if (isGameOver) {
            result = result
                    .withGameOver(true)
                    .withWinner(owner)
                    .withNextStateHint(NextStateHint.GAME_ENDED);
        } else if (hasMoreResults) {
            result = result.withNextStateHint(NextStateHint.STAY);
        } else {
            result = result.withNextStateHint(NextStateHint.NEXT_TURN);
        }

        return result;
    }

}


