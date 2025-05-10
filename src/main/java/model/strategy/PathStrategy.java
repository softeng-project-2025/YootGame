package model.strategy;

import model.piece.Piece;
import model.position.Position;
import model.yut.YutResult;

import java.util.List;

public interface PathStrategy {
    /**
     * 현재 위치와 윷 결과에 따라 다음 위치를 반환
     */
    Position getNextPosition(Piece piece, YutResult result);;

    Position getPreviousPosition(Piece piece, YutResult result);
    /**
     * 전체 경로(Position 리스트) 반환 (초기화, 렌더링용)
     */
    List<Position> getPath();

    List<Position> getAllPositions();

    List<Position> getAllVertexPositions();
}