package model.board;

import model.piece.Piece;
import model.position.Position;
import model.yut.YutResult;
import model.strategy.PathStrategy;

public class Board {

    private PathStrategy pathStrategy;

    public Board(PathStrategy pathStrategy) {
        this.pathStrategy = pathStrategy;
    }

    // Piece가 현재 위치에서 YutResult에 따라 다음 위치로 이동
    public Position getNextPosition(Position current, YutResult result) {
        return pathStrategy.getNextPosition(current, result);
    }

    public PathStrategy getPathStrategy() {
        return pathStrategy;
    }

    public void setPathStrategy(PathStrategy pathStrategy) {
        this.pathStrategy = pathStrategy;
    }

    public void movePiece(Piece piece, YutResult result) {
        Position next = getNextPosition(piece.getPosition(), result);
        piece.setPosition(next);
    }
}