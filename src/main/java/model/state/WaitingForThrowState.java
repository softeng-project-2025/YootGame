package model.state;

import model.Game;
import model.dto.GameMessage;
import model.dto.GameMessageFactory;
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

        // 메시지 설정을 GameMessageFactory에서 처리
        GameMessage msg = GameMessageFactory.yutThrownPrompt(
                game.getCurrentPlayer().getName(),
                result.getName()
        );
        game.setLastMessage(msg);

        return MoveResult.success(false, false, game.isFinished() ? game.getCurrentPlayer() : null, game);
    }

    @Override
    public MoveResult handlePieceSelectWithResult(Piece piece) {
        game.setLastMessage(GameMessageFactory.throwRequiredMessage());
        return MoveResult.fail();
    }

}