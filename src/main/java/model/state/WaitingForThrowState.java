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
    public MoveResult handleYutThrow(YutResult result) {
        game.getTurnResult().add(result);
        return MoveResult.success(
                result,
                false,
                false,
                game.isFinished() ? game.getTurnManager().currentPlayer() : null,
                game
        ).withNextStateHint(null); // 상태 전이는 GameService에서 처리
    }

}