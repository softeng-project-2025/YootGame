package model.board;

import model.piece.Piece;
import model.piece.PieceUtil;
import model.position.Position;
import model.yut.YutResult;
import model.strategy.PathStrategy;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    // 선택된 Piece와 그 그룹의 다음 위치를 계산합니다 (부작용 없음).
    public Map<Piece, Position> computeNextPositions(Piece piece, YutResult result) {
        List<Piece> group = piece.getGroup().isEmpty()
                ? List.of(piece)
                : piece.getGroup();
        Map<Piece, Position> nextPositions = new LinkedHashMap<>();

        for (Piece p : group) {
            // Path 초기화
            if (p.getCustomPath() == null) {
                PieceUtil.initializePath(p, strategy);
            }
            // 다음 위치 계산
            Position next = result.getStep() < 0
                    ? strategy.getPreviousPosition(p.getPosition())
                    : strategy.getNextPosition(p, result);

            nextPositions.put(p, next);
        }
        return nextPositions;
    }

    // computeNextPositions로 계산된 위치를 실제 Piece에 적용합니다.
    public void applyMovement(Map<Piece, Position> nextPositions, YutResult result) {
        for (var entry : nextPositions.entrySet()) {
            Piece p   = entry.getKey();
            Position pos = entry.getValue();

            p.setPosition(pos);
            p.advancePathIndex(result.getStep());

            // 경로 끝 도달 시 완료 처리
            if (p.getCustomPath() != null
                    && p.getPathIndex() == p.getCustomPath().size() - 1) {
                p.setFinished(true);
            }
            p.setMoved(true);
        }
    }


}