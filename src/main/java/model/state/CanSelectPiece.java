package model.state;

import model.dto.MoveResult;
import model.piece.Piece;

// 말 선택만 허용하는 상태
public interface CanSelectPiece extends GameState {
    MoveResult handlePieceSelectWithResult(Piece piece);
}