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

    private final List<Position> allPositions;
    private final List<Position> allVertexPositions;
    private final List<Position> outerPath;
    private final List<Position> pathFrom5;
    private final List<Position> pathFrom10;
    private final List<Position> pathFrom5Center;

    public SquarePathStrategy() {
        allPositions = createAllPositions();
        allVertexPositions = createAllVertexPositions();
        outerPath = createOuterPath();
        pathFrom5 = createPathFrom5();
        pathFrom10 = createPathFrom10();
        pathFrom5Center = createPathFrom5Center();
    }

    // 경로 업데이트 & 업데이트된 경로 기반 다음 위치
    @Override
    public Position getNextPosition(Piece piece, YutResult result) {
        PathType pathType = piece.getPathType();
        int pathIndex = piece.getPathIndex();

        // 모에 있었을 때
        if (pathIndex == 5) {
            piece.setPathType(PathType.FROM5);
            piece.setCustomPath(pathFrom5);
            piece.setPathIndex(5);
        }

        // 모에 있었다가 백도 받아서 윷에 있었을 때
        if (
                pathType == PathType.FROM5
                && pathIndex == 4
        ) {
            piece.setPathType(PathType.OUTER);
            piece.setCustomPath(outerPath);
            piece.setPathIndex(4);
        }

        // 뒷모에 있었을 때
        if (pathIndex == 10) {
            piece.setPathType(PathType.FROM10);
            piece.setCustomPath(pathFrom10);
            piece.setPathIndex(10);
        }

        // 뒷모에 있었다가 백도를 받아서 뒷윷에 있었을 때
        if (
                pathType == PathType.FROM10
                && pathIndex == 9
        ) {
            piece.setPathType(PathType.OUTER);
            piece.setCustomPath(outerPath);
            piece.setPathIndex(9);
        }

        // 모도, 모개로 와서 방에 있었을 때
        if (
                pathType == PathType.FROM5
                && pathIndex == 8
        ) {
            piece.setPathType(PathType.FROM5CENTER);
            piece.setCustomPath(pathFrom5Center);
            piece.setPathIndex(8);
        }

        // from5이면서 방 들어갔다가 빽도로 나오면 pathFrom5로 변경
        if (
                pathType == PathType.FROM5CENTER
                && pathIndex == 7
        ) {
            piece.setPathType(PathType.FROM5);
            piece.setCustomPath(pathFrom5);
            piece.setPathIndex(7);
        }

        List<Position> path = piece.getCustomPath();
        pathIndex = piece.getPathIndex();
        int nextIdx = pathIndex + result.getStep();

        return path.get(nextIdx);
    }

    @Override
    public Position getPreviousPosition(Piece piece, YutResult result) {
        int pathIndex = piece.getPathIndex();
        if (pathIndex == 1) {
            piece.setPathType(PathType.OUTER);
            piece.setCustomPath(outerPath);
            piece.setPathIndex(20);
        }

        List<Position> path = piece.getCustomPath();
        pathIndex = piece.getPathIndex();

        return path.get(pathIndex - 1);
    }

    @Override
    public List<Position> getPath() {
        return outerPath;
    }

    @Override
    public List<Position> getAllPositions() {
        return allPositions;
    }

    @Override
    public List<Position> getAllVertexPositions() {
        return allVertexPositions;
    }

    // for drawing board's noon
    private List<Position> createAllPositions() {
        List<Position> positions = new ArrayList<>();
        int[][] allCoords = {
                {5000, 5000},
                {600, 480},
                {600, 360},
                {600, 240},
                {600, 120},
                {600, 0},   // 5
                {480, 0},
                {360, 0},
                {240, 0},
                {120, 0},
                {0, 0},     // 10
                {0, 120},
                {0, 240},
                {0, 360},
                {0, 480},
                {0, 600},   // 15
                {120, 600},
                {240, 600},
                {360, 600},
                {480, 600}, // 19
                {600, 600}, // 20
                {500, 100},
                {400, 200},
                {100, 100},
                {200, 200},
                {200, 400},
                {100, 500},
                {400, 400},
                {500, 500},
                {300, 300}, // 29, center
        };

        for (int i = 0; i < allCoords.length; i++) {
            int x = allCoords[i][0];
            int y = allCoords[i][1];
            boolean isVertex = (i <= 20 && i % 5 == 0);
            boolean isCenter = (i == allCoords.length - 1);
            positions.add(new Position(i, x, y, isCenter, isVertex));
        }

        return positions;
    }

    // for drawing board's background line
    private List<Position> createAllVertexPositions() {
        List<Position> positions = new ArrayList<>();
        int[][] allCoords = {
                {600, 600},
                {600, 0},

                {600, 0},
                {0, 0},

                {0, 0},
                {0, 600},

                {0, 600},
                {600, 600},

                {300, 300},
                {600, 600},

                {300, 300},
                {600, 0},

                {300, 300},
                {0, 0},

                {300, 300},
                {0, 600},
        };

        for (int i = 0; i < allCoords.length; i++) {
            int x = allCoords[i][0];
            int y = allCoords[i][1];
            positions.add(new Position(i, x, y));
        }

        return positions;
    }

    private List<Position> createOuterPath() {
        int[][] coords = {
                {5000, 5000},   // 출발 전, Piece의 startPos 따로 참고
                {600, 480},
                {600, 360},
                {600, 240},
                {600, 120},
                {600, 0},   // 모
                {480, 0},
                {360, 0},
                {240, 0},
                {120, 0},
                {0, 0},     // 뒷모
                {0, 120},
                {0, 240},
                {0, 360},
                {0, 480},
                {0, 600},   // 찌모
                {120, 600},
                {240, 600},
                {360, 600},
                {480, 600},
                {600, 600}, // 참먹이
                {5000, 5000}    // 탈출
        };

        return createPath(coords);
    }

    private List<Position> createPathFrom5() {
        int[][] coords = {
                {5000, 5000},   // 출발 전, Piece의 startPos 따로 참고
                {600, 480},
                {600, 360},
                {600, 240},
                {600, 120},
                {600, 0},
                {500, 100},
                {400, 200},
                {300, 300}, // 28, center
                {400, 400},
                {500, 500},
                {0, 600},
                {120, 600},
                {240, 600},
                {360, 600},
                {480, 600}, // 19
                {600, 600}, // 참먹이
                {5000, 5000}    // 탈출
        };

        return createPath(coords);
    }

    private List<Position> createPathFrom10() {
        int[][] coords = {
                {5000, 5000},   // 출발 전, Piece의 startPos 따로 참고
                {600, 480},
                {600, 360},
                {600, 240},
                {600, 120},
                {600, 0},
                {480, 0},
                {360, 0},
                {240, 0},
                {120, 0},
                {0, 0},     // 뒷모
                {100, 100},
                {200, 200},
                {300, 300},
                {400, 400},
                {500, 500},
                {600, 600}, // 참먹이
                {5000, 5000}    // 탈출
        };

        return createPath(coords);
    }

    private List<Position> createPathFrom5Center() {
        int[][] coords = {
                {5000, 5000},   // 출발 전, Piece의 startPos 따로 참고
                {600, 480},
                {600, 360},
                {600, 240},
                {600, 120},
                {600, 0},
                {500, 100},
                {400, 200},
                {300, 300},
                {400, 400},
                {500, 500},
                {600, 600}, // 참먹이
                {5000, 5000}    // 탈출
        };

        return createPath(coords);
    }

    private List<Position> createPath(int[][] coords) {
        List<Position> path = new ArrayList<>();
        for (int i = 0; i < coords.length; i++) {
            path.add(new Position(i, coords[i][0], coords[i][1]));
        }
        return path;
    }

}