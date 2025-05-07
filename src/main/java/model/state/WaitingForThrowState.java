package model.state;

import model.Game;
import model.dto.MoveResult;
import model.yut.YutResult;

public class WaitingForThrowState implements CanThrowYut {

    private final Game game;

    public WaitingForThrowState(Game game) {
        this.game = game;
    }

    @Override
    public MoveResult handleYutThrowWithResult(YutResult result) {
        game.enqueueYutResult(result);
        return MoveResult.success(
                false,
                false,
                game.isFinished() ? game.getCurrentPlayer() : null,
                game
        );
    }

}