package model.strategy;

import model.piece.Piece;
import model.position.Position;
import model.yut.YutResult;

import java.util.List;

public class PentagonPathStrategy implements PathStrategy {

    @Override
    public Position getNextPosition(Piece piece, YutResult result) {
        // TODO: 중심점 판단 및 1번 path vs 2번 path 규칙 구현
        throw new UnsupportedOperationException("PentagonPathStrategy is not implemented yet.");
    }

    @Override
    public java.util.List<Position> getPath() {
        // TODO: 오각형 경로 규칙 구현
        throw new UnsupportedOperationException("PentagonPathStrategy path not defined.");
    }

    @Override
    public List<Position> getAllPositions() {
        // TODO: 오각형 모든 좌표 제공 구현
        throw new UnsupportedOperationException("PentagonPathStrategy path not defined.");
    }

    @Override
    public List<Position> getAllVertexPositions() {
        // TODO: 오각형 모든 좌표 제공 구현
        throw new UnsupportedOperationException("PentagonPathStrategy path not defined.");
    }

    @Override
    public Position getPreviousPosition(Position current){
        // TODO: 오각형 경로 규칙 구현
        throw new UnsupportedOperationException("PentagonPathStrategy path not defined.");
    }
}