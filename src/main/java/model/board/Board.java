package model.board;

import exception.InvalidMoveException;
import model.piece.Piece;
import model.piece.PieceUtil;
import model.position.Position;
import model.yut.YutResult;
import model.strategy.PathStrategy;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    // 주어진 Piece를 result만큼 이동시킬 다음 위치를 계산합니다.
    public Position computeNextPosition(Piece piece, YutResult result) {
        if (piece.isFinished()) {
            throw new InvalidMoveException("완주된 말은 이동할 수 없습니다.");
        }
        // 경로 초기화
        if (piece.getCustomPath() == null) {
            PieceUtil.initializePath(piece, strategy);
        }
        // 이전/다음 위치 계산
        return result.getStep() < 0
                ? strategy.getPreviousPosition(piece.getPosition())
                : strategy.getNextPosition(piece, result);
    }

    // computeNextPosition 결과를 꺼내 Piece 상태를 업데이트합니다.
    public void applyMovement(Piece piece, Position newPos, YutResult result) {
        piece.moveTo(newPos, result.getStep());
    }


}