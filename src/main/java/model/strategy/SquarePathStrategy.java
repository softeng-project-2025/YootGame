package model.strategy;

import model.position.Position;
import model.yut.YutResult;

import java.util.ArrayList;
import java.util.List;

public class SquarePathStrategy implements PathStrategy {

    private final List<Position> path;

    public SquarePathStrategy() {
        path = createSquarePath();
    }

    // 경로 기반 이동 로직
    @Override
    public Position getNextPosition(Position current, YutResult result) {
        int currentIndex = current.getIndex();
        int nextIndex = currentIndex + result.getStep();

        if (nextIndex >= path.size()) {
            // 도착 처리: 마지막 지점 도달
            return path.get(path.size() - 1);
        }
        return path.get(nextIndex);
    }

    public List<Position> getPath() {
        return path;
    }

    private List<Position> createSquarePath() {
        List<Position> positions = new ArrayList<>();

        // 예시 좌표: 시계방향 정사각형 (간단 버전)
        int[][] coords = {
                {100, 500}, {100, 400}, {100, 300}, {100, 200}, {100, 100},
                {200, 100}, {300, 100}, {400, 100}, {500, 100},
                {500, 200}, {500, 300}, {500, 400}, {500, 500},
                {400, 500}, {300, 500}, {200, 500},
                {200, 400}, {200, 300}, {200, 200},
                {300, 200}, {400, 200},
                {400, 300}, {400, 400},
                {300, 400}, {300, 300} // 중심점
        };

        for (int i = 0; i < coords.length; i++) {
            int x = coords[i][0];
            int y = coords[i][1];
            boolean isCenter = (x == 300 && y == 300);
            positions.add(new Position(i, x, y, isCenter));
        }

        return positions;
    }
}