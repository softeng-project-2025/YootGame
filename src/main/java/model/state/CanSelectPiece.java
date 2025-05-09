package model.state;

import model.dto.MoveResult;
import model.piece.Piece;
import model.yut.YutResult;

// 말 선택만 허용하는 상태
public interface CanSelectPiece extends GameState {
    MoveResult handlePieceSelect(Piece piece, YutResult result);
}