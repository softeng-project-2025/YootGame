package model.strategy;

import model.piece.Piece;
import model.position.Position;
import model.yut.YutResult;

import java.util.List;


public class HexPathStrategy implements PathStrategy {

    @Override
    public  Position getNextPosition(Piece piece, YutResult result) {
        // TODO: 육각형 경로 규칙 구현
        throw new UnsupportedOperationException("HexPathStrategy is not implemented yet.");
    }


    @Override
    public java.util.List<Position> getPath() {
        // TODO: 육각형 경로 규칙 구현
        throw new UnsupportedOperationException("HexPathStrategy path not defined.");
    }

    @Override
    public List<Position> getAllPositions() {
        // TODO: 육각형 모든 좌표 제공 구현
        throw new UnsupportedOperationException("HexPathStrategy path not defined.");
    }

    @Override
    public List<Position> getAllVertexPositions() {
        // TODO: 육각형 모든 좌표 제공 구현
        throw new UnsupportedOperationException("HexPathStrategy path not defined.");
    }

    @Override
    public Position getPreviousPosition(Piece piece, YutResult result){
        // TODO: 육각형 경로 규칙 구현
        throw new UnsupportedOperationException("HexPathStrategy path not defined.");
    }
}