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
        this.lastResult = result;

        String message = game.getCurrentPlayer().getName() + " 이(가) 윷을 던졌습니다: " + result.getName();

        List<Piece> movable = PieceUtil.getMovablePieces(game.getCurrentPlayer(), result);

        if (movable.isEmpty()) {
            game.setLastMessage(new GameMessage(message + "그러나 이동할 수 있는 말이 없어 턴을 넘깁니다." , MessageType.INFO));
            return new MoveResult(
                    false, // captured
                    false, // gameFinished
                    null,
                    false, // bonusTurn
                    true   // turnSkipped
            );
        } else {
            game.setState(new SelectingPieceState(game, result));
            game.setLastMessage(new GameMessage(message + " 말을 선택하세요." , MessageType.INFO));
            return new MoveResult(
                    false, false, null, false, false
            );
        }
    }

    @Override
    public MoveResult handlePieceSelectWithResult(Piece piece) {

        game.setLastMessage(new GameMessage("아직 윷을 던지지 않았습니다!" , MessageType.WARN));
        return new MoveResult(false, false, null, false, false);
    }

    @Override
    public YutResult getLastYutResult() {
        return lastResult;
    }
}