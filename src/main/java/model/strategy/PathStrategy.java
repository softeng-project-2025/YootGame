package model.strategy;

import model.position.Position;
import model.yut.YutResult;

public interface PathStrategy {
    /**
     * 현재 위치와 윷 결과에 따라 다음 위치를 반환
     */
    Position getNextPosition(Position current, YutResult result);

    /**
     * 전체 경로(Position 리스트) 반환 (초기화, 렌더링용)
     */
    java.util.List<Position> getPath();
    Position getPreviousPosition(Position current, int steps);
}