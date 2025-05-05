package model.state;

import model.Game;
import model.dto.GameMessage;
import model.dto.MessageType;
import model.dto.MoveResult;
import model.piece.Piece;
import model.piece.PieceUtil;
import model.yut.YutResult;

import java.util.List;

public class WaitingForThrowState implements GameState {

    private final Game game;
    private YutResult lastResult;

    public WaitingForThrowState(Game game) {
        this.game = game;
    }

    @Override
    public MoveResult handleYutThrowWithResult(YutResult result) {
        game.enqueueYutResult(result);

        String message = game.getCurrentPlayer().getName() + "이(가) 윷을 던졌습니다: " + result.getName();
        GameMessage msg = new GameMessage(message, MessageType.INFO);
        game.setLastMessage(msg);

        return new MoveResult(false, false, null, false, false);
    }

    @Override
    public MoveResult handlePieceSelectWithResult(Piece piece) {

        game.setLastMessage(new GameMessage("아직 윷을 던지지 않았습니다!" , MessageType.WARN));
        return new MoveResult(false, false, null, false, false);
    }

}