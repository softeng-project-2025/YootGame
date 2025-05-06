package model.state;

import model.Game;
import model.dto.*;
import model.piece.Piece;
import model.piece.PieceUtil;
import model.yut.YutQueueHandler;
import model.yut.YutResult;

import java.util.List;

public class SelectingPieceState implements GameState {

    private final Game game;

    public SelectingPieceState(Game game, YutResult result) {
        this.game = game;
        this.game.enqueueYutResult(result);
    }

    @Override
    public MoveResult handleYutThrowWithResult(YutResult result) {
        return MoveResult.fail(MoveFailType.ALREADY_THROWN);
    }

    @Override
    public MoveResult handlePieceSelectWithResult(Piece piece) {
        if (piece.isFinished() || piece.getOwner() != game.getCurrentPlayer()) {
            return MoveResult.fail(MoveFailType.INVALID_SELECTION);
        }

        YutResult moveResult = YutQueueHandler.dequeueResult(game);
        if (moveResult == null) {
            return MoveResult.fail(MoveFailType.NO_RESULT);
        }

        // 그룹 설정
        List<Piece> group = PieceUtil.getMovableGroup(piece, game);

        // 이동 처리
        boolean captured = game.getBoard().movePiece(piece, moveResult, game.getPlayers());

        String playerName = piece.getOwner().getName();
        if (PieceUtil.allFinished(group, game.getBoard())) {
            for (Piece p : group) {
                p.setFinished(true);
                PieceUtil.resetGroupToSelf(p);
            }
            return MoveResult.goal(piece.getOwner(), game);
        }

        // 추가 턴 조건
        boolean isYutOrMo = moveResult == YutResult.YUT || moveResult == YutResult.MO;
        boolean bonusTurn = (captured && !isYutOrMo) || (isYutOrMo && !captured);

        // 다음 상태 결정
        if (game.hasPendingYutResults()) {
            return MoveResult.success(captured, false, game.isFinished() ? piece.getOwner() : null, game, piece, true);
        }

        if (bonusTurn) {
            game.setState(new WaitingForThrowState(game));
            return MoveResult.success(captured, true, game.isFinished() ? piece.getOwner() : null, game, piece, false);
        }

        // 턴 종료
        game.nextTurn();
        return MoveResult.success(captured, false, game.isFinished() ? piece.getOwner() : null, game, piece, false);
    }

}


