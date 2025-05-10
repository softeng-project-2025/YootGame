package model.state;

import model.dto.MoveFailType;
import model.dto.MoveResult;
import model.piece.Piece;
import model.yut.YutResult;

// 게임이 종료된 후 더 이상 동작을 허용하지 않는 상태를 표현합니다.
public class GameOverState implements CanThrowYut, CanSelectPiece {

    @Override
    public MoveResult handleYutThrow(YutResult result) {
        // 게임 종료 상태에서는 윷 던지기를 허용하지 않습니다.
        return MoveResult.fail(MoveFailType.GAME_ENDED);
    }

    @Override
    public MoveResult handlePieceSelect(Piece piece, YutResult result) {
        // 게임 종료 상태에서는 말 선택을 허용하지 않습니다.
        return MoveResult.fail(MoveFailType.GAME_ENDED);
    }
}