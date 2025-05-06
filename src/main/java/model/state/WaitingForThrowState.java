package model.state;

import model.Game;
import model.dto.GameMessage;
import model.dto.GameMessageFactory;
import model.dto.MoveFailType;
import model.dto.MoveResult;
import model.piece.Piece;
import model.yut.YutResult;

public class WaitingForThrowState implements GameState {

    private final Game game;

    public WaitingForThrowState(Game game) {
        this.game = game;
    }

    @Override
    public MoveResult handleYutThrowWithResult(YutResult result) {
        game.enqueueYutResult(result);
        return MoveResult.success(false, false, game.isFinished() ? game.getCurrentPlayer() : null, game);
    }

    @Override
    public MoveResult handlePieceSelectWithResult(Piece piece) {
        return MoveResult.fail(MoveFailType.THROW_REQUIRED);
    }

}