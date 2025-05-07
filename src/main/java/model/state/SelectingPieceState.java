package model.state;

import model.Game;
import model.dto.*;
import model.piece.Piece;
import model.piece.PieceUtil;
import model.yut.YutQueueHandler;
import model.yut.YutResult;

import java.util.List;

public class SelectingPieceState implements CanSelectPiece {

    private final Game game;

    public SelectingPieceState(Game game, YutResult result) {
        this.game = game;
        this.game.enqueueYutResult(result);
    }

    @Override
    public MoveResult handlePieceSelectWithResult(Piece piece) {
        // 유효성 검사
        if (piece.isFinished() || piece.getOwner() != game.getCurrentPlayer()) {
            return MoveResult.fail(MoveFailType.INVALID_SELECTION);
        }

        YutResult moveResult = YutQueueHandler.dequeueResult(game);
        if (moveResult == null) {
            return MoveResult.fail(MoveFailType.NO_RESULT);
        }

        // 그룹 이동
        List<Piece> group = PieceUtil.getMovableGroup(piece, game);
        boolean captured = game.getBoard().movePiece(piece, moveResult, game.getPlayers());

        // 골인 체크
        var owner = piece.getOwner();
        if (PieceUtil.allFinished(group, game.getBoard())) {
            group.forEach(p -> {
                p.setFinished(true);
                PieceUtil.resetGroupToSelf(p);
            });
            return MoveResult.goal(owner, game);
        }

        // 보너스 턴 조건
        boolean isYutOrMo = moveResult == YutResult.YUT || moveResult == YutResult.MO;
        boolean bonusTurn = (captured && !isYutOrMo) || (isYutOrMo && !captured);
        boolean hasMoreResults = game.hasPendingYutResults();

        // 다음 상태 결정
        return MoveResult.success(
                captured,
                bonusTurn,
                game.isFinished() ? owner : null,
                game,
                piece,
                hasMoreResults
        );
    }

}


