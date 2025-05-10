package model.board;

import exception.InvalidMoveException;
import model.piece.Piece;
import model.piece.PieceUtil;
import model.position.Position;
import model.yut.YutResult;
import model.strategy.PathStrategy;


// Board: 순수 이동 계산 및 적용만 담당합니다.
public class Board {

    private PathStrategy strategy;

    public Board(PathStrategy strategy) {
        this.strategy = strategy;
    }

    public PathStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(PathStrategy strategy) {
        this.strategy = strategy;
    }

    public Position movePiece(Piece piece, YutResult result) {
        if (piece.isFinished()) {
            throw new InvalidMoveException("완주된 말은 이동할 수 없습니다.");
        }
        // 경로 초기화
        if (piece.getCustomPath() == null) {
            piece.resetToStart();
        }
        // 다음 위치 계산
        Position next = result.getStep() < 0
                ? strategy.getPreviousPosition(piece, result)
                : strategy.getNextPosition(piece, result);
        // 위치 업데이트
        piece.moveTo(next, result.getStep());
        return next;
    }



}