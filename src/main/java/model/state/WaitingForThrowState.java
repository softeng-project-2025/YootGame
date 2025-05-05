package model.state;

import model.Game;
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
        System.out.println("[INFO] " + message);

        List<Piece> movable = PieceUtil.getMovablePieces(game.getCurrentPlayer(), result);

        if (movable.isEmpty()) {
            return new MoveResult(
                    message + " 그러나 이동할 수 있는 말이 없어 턴을 넘깁니다.",
                    false, // captured
                    false, // gameFinished
                    null,
                    false, // bonusTurn
                    true   // turnSkipped
            );
        } else {
            game.setState(new SelectingPieceState(game, result));
            return new MoveResult(
                    message + " 말을 선택하세요.",
                    false, false, null,
                    false, false
            );
        }
    }

    @Override
    public MoveResult handlePieceSelectWithResult(Piece piece) {
        String warning = "아직 윷을 던지지 않았습니다!";
        System.out.println("[WARN] " + warning);
        return new MoveResult(warning, false, false, null, false, false);
    }

    @Override
    public YutResult getLastYutResult() {
        return lastResult;
    }
}