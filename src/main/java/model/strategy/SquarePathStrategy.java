package model.strategy;

import model.piece.Piece;
import model.position.Position;
import model.yut.YutResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.piece.PathType;

public class SquarePathStrategy implements PathStrategy {

    private final Map<Integer, Position> positionPool = new HashMap<>();
    private final List<Position> allPositions;
    private final List<Position> outerPath;
    private final List<Position> pathFrom5;
    private final List<Position> pathFrom10;

    public static final int CENTER_INDEX = 28;

    public SquarePathStrategy() {
        allPositions = createAllPositions();
        outerPath = createOuterPath();
        pathFrom5 = createDiagonalPathFrom5(false);
        pathFrom10 = createDiagonalPathFrom10();
    }

    private Position pos(int index, int x, int y) {
        return positionPool.computeIfAbsent(index, i -> new Position(i, x, y));
    }

    private Position pos(int index, int x, int y, boolean isCenter) {
        return positionPool.computeIfAbsent(index, i -> new Position(i, x, y, isCenter));
    }

    // 경로 기반 이동 로직
    @Override
    public Position getNextPosition(Piece piece, YutResult result) {
        // 대각선 진입 조건
        if (piece.getPathType() == PathType.OUTER) {
            if (piece.getPosition().getIndex() == 5 && shouldEnterDiagonalFrom5(piece)) {
                piece.setPathType(PathType.DIAGONAL_5);
                piece.setCustomPath(createDiagonalPathFrom5());
                piece.setPathIndex(1); // 5 → 20
                return piece.getCustomPath().get(1);
            }
            else if (piece.getPosition().getIndex() == 10) {
                piece.setPathType(PathType.DIAGONAL_10);
                piece.setCustomPath(createDiagonalPathFrom10());
                piece.setPathIndex(1); // 10 → 22
                return piece.getCustomPath().get(1);
            }
        }

        List<Position> path = piece.getCustomPath();
        int idx = piece.getPathIndex();
        int nextIdx = idx + result.getStep();

        if (nextIdx >= path.size()) {
            nextIdx = path.size() - 1;
        }

        Position nextPos = path.get(nextIdx);

        // 중심 도달 시 경로 전환 처리
        if (!piece.hasPassedCenter() && nextPos.getIndex() == CENTER_INDEX) {
            piece.setPassedCenter(true); // 상태 플래그 true

            // 중심 도달 후 path 교체
            if (piece.getPathType() == PathType.DIAGONAL_5) {
                piece.setCustomPath(createCenterToExitPath());
                piece.setPathType(PathType.DIAGONAL_5); // 유지
                piece.setPathIndex(0); // 중심 → 출구 경로 시작점부터
                return piece.getCustomPath().get(0);
            }
        }

        return nextPos;
    }

    @Override
    public Position getPreviousPosition(Position current, int steps) {
        int currentIndex = current.getIndex();
        int prevIndex = currentIndex - steps;
        if (prevIndex < 0) return outerPath.get(0); // 출발점보다 뒤로 갈 수 없음
        return outerPath.get(prevIndex);
    }

    public List<Position> getPath() {
        return outerPath;
    }

    public List<Position> getAllPositions() {
        return allPositions;
    }



    private List<Position> createAllPositions() {
        List<Position> positions = new ArrayList<>();

        // 외곽 0~19 (시작: 오른쪽 하단 → 위로 반시계방향)
        int[][] outerCoords = {
                {500, 500}, // 0 시작점 (오른쪽 하단)
                {500, 400}, // 1 위
                {500, 300}, // 2
                {500, 200}, // 3
                {500, 100}, // 4
                {500,   0}, // 5 ↖ 대각선 진입점
                {400,   0}, // 6 ←
                {300,   0}, // 7
                {200,   0}, // 8
                {100,   0}, // 9
                {  0,   0}, // 10 ↙ 대각선 진입점
                {  0, 100}, // 11 ↓
                {  0, 200}, // 12
                {  0, 300}, // 13
                {  0, 400}, // 14
                {  0, 500}, // 15 → 우회로 계속
                {100, 500}, // 16 →
                {200, 500}, // 17
                {300, 500}, // 18
                {400, 500}, // 19
        };

        for (int i = 0; i < outerCoords.length; i++) {
            int x = outerCoords[i][0];
            int y = outerCoords[i][1];
            boolean isDiagonalEntry = (i == 5 || i == 10);
            positions.add(new Position(i, x, y, false, isDiagonalEntry));
        }

        // 5 → 20 → 21 → 28(중심)
        positions.add(pos(20, 400, 100));
        positions.add(pos(21, 300, 200));
        positions.add(pos(22, 100, 200));
        positions.add(pos(23, 200, 300));
        positions.add(pos(24, 200, 400));
        positions.add(pos(25, 100, 500));
        positions.add(pos(26, 400, 400));
        positions.add(pos(27, 500, 400));
        positions.add(pos(28, 300, 300, true));

        return positions;
    }

    private List<Position> createOuterPath() {

        int[][] coords = {
                {500, 500}, // 0 시작점 (오른쪽 하단)
                {500, 400}, // 1 위
                {500, 300}, // 2
                {500, 200}, // 3
                {500, 100}, // 4
                {500,   0}, // 5 ↖ 대각선 진입점
                {400,   0}, // 6 ←
                {300,   0}, // 7
                {200,   0}, // 8
                {100,   0}, // 9
                {  0,   0}, // 10 ↙ 대각선 진입점
                {  0, 100}, // 11 ↓
                {  0, 200}, // 12
                {  0, 300}, // 13
                {  0, 400}, // 14
                {  0, 500}, // 15 → 우회로 계속
                {100, 500}, // 16 →
                {200, 500}, // 17
                {300, 500}, // 18
                {400, 500}, // 19
        };

        List<Position> path = new ArrayList<>();
        for (int i = 0; i < coords.length; i++) {
            path.add(pos(i, coords[i][0], coords[i][1]));
        }
        return path;
    }

    private List<Position> createDiagonalPathFrom5(boolean passedCenter) {
        List<Position> path = new ArrayList<>();
        // 5 → 20 → 21 → 28(중심)
        path.add(pos(5, 500, 0)); // 시작점
        path.add(pos(20, 400, 100));
        path.add(pos(21, 300, 200));
        path.add(pos(28, 300, 300, true)); // 중심점

        if (passedCenter) {
            // 중심을 밟았다면: 중심 → 26 → 27 → 0 (출구)
            path.add(pos(26, 400, 400));
            path.add(pos(27, 500, 400));
            path.add(pos(0, 500, 500)); // 종료점
        } else {
            // 중심 안 밟음: 24 → 25 → 15 → 16 → 17 → 18 → 19 → 0
            path.add(pos(24, 200, 400));
            path.add(pos(25, 100, 500));
            path.add(pos(15, 0, 500));
            path.add(pos(16, 100, 500));
            path.add(pos(17, 200, 500));
            path.add(pos(18, 300, 500));
            path.add(pos(19, 400, 500));
            path.add(pos(0, 500, 500)); // 종료점
        }
        return path;
    }
    private List<Position> createDiagonalPathFrom10() {
        List<Position> path = new ArrayList<>();
        path.add(pos(10, 0, 100));      // 진입점 (왼쪽 중간)
        path.add(pos(22, 100, 200));    // 대각선 이동
        path.add(pos(23, 200, 300));
        path.add(pos(28, 300, 300, true)); // 중심점
        path.add(pos(26, 400, 400));    // 중심 이후
        path.add(pos(27, 500, 400));
        path.add(pos(0, 500, 500));     // 출구
        return path;
    }

    private int findIndex(List<Position> path, Position pos) {
        for (int i = 0; i < path.size(); i++) {
            if (path.get(i).getIndex() == pos.getIndex()) return i;
        }
        return -1;
    }

    private boolean shouldEnterDiagonalFrom5(Piece piece) {
        // 중심을 이미 지난 적이 있다면 안 들어감
        if (piece.hasPassedCenter()) return false;

        // 앞으로 가야 할 거리 계산: OUTER vs DIAGONAL
        int currentIndex = findIndex(outerPath, piece.getPosition());
        int outerStepsLeft = outerPath.size() - currentIndex;
        int diagonalSteps = pathFrom5.size(); // 5 → ... → 0

        return diagonalSteps < outerStepsLeft;
    }

    private List<Position> createCenterToExitPath() {
        List<Position> path = new ArrayList<>();
        path.add(pos(28, 300, 300, true)); // 중심
        path.add(pos(26, 400, 400));
        path.add(pos(27, 500, 400));
        path.add(pos(0, 500, 500)); // 출구
        return path;
    }

    private List<Position> createDiagonalPathFrom5() {
        List<Position> path = new ArrayList<>();
        path.add(pos(5, 500, 0));
        path.add(pos(20, 400, 100));
        path.add(pos(21, 300, 200));
        path.add(pos(28, 300, 300, true)); // 중심 도달 시 동적으로 교체됨
        return path;
    }


}