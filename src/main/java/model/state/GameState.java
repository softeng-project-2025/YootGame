package model.state;

import model.dto.MoveResult;
import model.yut.YutResult;
import model.piece.Piece;

public interface GameState {
    // 윷을 던졌을 때 처리 (랜덤 or 지정)
    MoveResult handleYutThrowWithResult(YutResult result);

    // 말을 선택했을 때 처리
    MoveResult handlePieceSelectWithResult(Piece piece);

}
