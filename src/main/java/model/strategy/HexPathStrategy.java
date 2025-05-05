package model.strategy;

import model.piece.Piece;
import model.position.Position;
import model.yut.YutResult;


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
    public Position getPreviousPosition(Position current, int steps){
        // TODO: 육각형 경로 규칙 구현
        throw new UnsupportedOperationException("HexPathStrategy path not defined.");
    }
}