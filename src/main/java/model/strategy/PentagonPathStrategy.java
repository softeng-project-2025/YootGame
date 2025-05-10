package model.strategy;

import model.piece.PathType;
import model.piece.Piece;
import model.position.Position;
import model.yut.YutResult;

import java.util.ArrayList;
import java.util.List;

public class PentagonPathStrategy implements PathStrategy {

    private final List<Position> allPositions;
    private final List<Position> allVertexPositions;
    private final List<Position> outerPath;
    private final List<Position> pathFrom5;
    private final List<Position> pathFrom10;
    private final List<Position> pathFrom15;
    private final List<Position> pathFrom5Center;
    private final List<Position> pathFrom10Center;
    private final List<Position> pathFrom15Center;

    public PentagonPathStrategy() {
        allPositions = createAllPositions();
        allVertexPositions = createAllVertexPositions();
        outerPath = createOuterPath();
        pathFrom5 = createPathFrom5();
        pathFrom10 = createPathFrom10();
        pathFrom15 = createPathFrom15();
        pathFrom5Center = createPathFrom5Center();
        pathFrom10Center = createPathFrom10Center();
        pathFrom15Center = createPathFrom15Center();
    }

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
        int prevIndex = pathIndex - 1;

        if (pathIndex == 1) {
            piece.setPathType(PathType.OUTER);
            piece.setCustomPath(outerPath);
            piece.setPathIndex(25);
            prevIndex = 25;
        }

        List<Position> path = piece.getCustomPath();
        return path.get(prevIndex);
    }

    @Override
    public java.util.List<Position> getPath() {
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
                {5000, 5000},   // 0
                {529, 526},
                {543, 452},
                {567, 378},
                {591, 304},
                {615, 230},     // 5
                {552, 184},
                {489, 138},
                {426, 92},
                {363, 46},
                {300, 0},       // 10
                {237, 46},
                {174, 92},
                {111, 138},
                {48, 184},
                {-15, 230},     // 15
                {9, 304},
                {33, 378},
                {57, 452},
                {81, 526},
                {105, 600},     // 20
                {183, 600},
                {261, 600},
                {339, 600},
                {417, 600},
                {495, 600},     // 25
                {510, 264},   // 26
                {405, 298},   // 27
                {300, 111},   // 28
                {300, 221},   // 29
                {90, 264},   // 30
                {195, 298},   // 31
                {235, 421},   // 32
                {170, 511},   // 33
                {365, 421},   // 34
                {430, 511},   // 35
                {300, 332},      // 36, center
        };

        for (int i = 0; i < allCoords.length; i++) {
            int x = allCoords[i][0];
            int y = allCoords[i][1];
            boolean isVertex = (i <= 25 && i % 5 == 0);
            boolean isCenter = (i == allCoords.length - 1);
            positions.add(new Position(i, x, y, isCenter, isVertex));
        }

        return positions;
    }

    // for drawing board's background line
    private List<Position> createAllVertexPositions() {
        List<Position> positions = new ArrayList<>();
        int[][] allCoords = {
                {495, 600},
                {615, 230},

                {615, 230},
                {300, 0},

                {300, 0},
                {-15, 230},

                {-15, 230},
                {105, 600},

                {105, 600},
                {495, 600},

                {300, 332},
                {495, 600},

                {300, 332},
                {615, 230},

                {300, 332},
                {300, 0},

                {300, 332},
                {-15, 230},

                {300, 332},
                {105, 600},
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
                {5000, 5000},   // 0
                {529, 526},
                {543, 452},
                {567, 378},
                {591, 304},
                {615, 230},     // 5
                {552, 184},
                {489, 138},
                {426, 92},
                {363, 46},
                {300, 0},       // 10
                {237, 46},
                {174, 92},
                {111, 138},
                {48, 184},
                {-15, 230},     // 15
                {9, 304},
                {33, 378},
                {57, 452},
                {81, 526},
                {105, 600},     // 20
                {183, 600},
                {261, 600},
                {339, 600},
                {417, 600},
                {495, 600},     // 25
                {5000, 5000}
        };

        return createPath(coords);
    }

    private List<Position> createPathFrom5() {
        int[][] coords = {
                {5000, 5000},   // 0
                {529, 526},
                {543, 452},
                {567, 378},
                {591, 304},
                {615, 230},     // 5
                {510, 264},   // 26
                {405, 298},   // 27
                {300, 332},      // 36, center
                {235, 421},   // 32
                {170, 511},   // 33
                {105, 600},     // 20
                {183, 600},
                {261, 600},
                {339, 600},
                {417, 600},
                {495, 600},     // 25
                {5000, 5000}
        };

        return createPath(coords);
    }

    private List<Position> createPathFrom10() {
        int[][] coords = {
                {5000, 5000},   // 0
                {529, 526},
                {543, 452},
                {567, 378},
                {591, 304},
                {615, 230},     // 5
                {552, 184},
                {489, 138},
                {426, 92},
                {363, 46},
                {300, 0},       // 10
                {300, 111},   // 28
                {300, 221},   // 29
                {300, 332},      // 36, center
                {235, 421},   // 32
                {170, 511},   // 33
                {105, 600},     // 20
                {183, 600},
                {261, 600},
                {339, 600},
                {417, 600},
                {495, 600},     // 25
                {5000, 5000}
        };

        return createPath(coords);
    }

    private List<Position> createPathFrom15() {
        int[][] coords = {
                {5000, 5000},   // 0
                {529, 526},
                {543, 452},
                {567, 378},
                {591, 304},
                {615, 230},     // 5
                {552, 184},
                {489, 138},
                {426, 92},
                {363, 46},
                {300, 0},       // 10
                {237, 46},
                {174, 92},
                {111, 138},
                {48, 184},
                {-15, 230},     // 15
                {90, 264},   // 30
                {195, 298},   // 31
                {300, 332},      // 36, center
                {235, 421},   // 32
                {170, 511},   // 33
                {105, 600},     // 20
                {183, 600},
                {261, 600},
                {339, 600},
                {417, 600},
                {495, 600},     // 25
                {5000, 5000}
        };

        return createPath(coords);
    }

    private List<Position> createPathFrom5Center() {
        int[][] coords = {
                {5000, 5000},   // 0
                {529, 526},
                {543, 452},
                {567, 378},
                {591, 304},
                {615, 230},     // 5
                {510, 264},   // 26
                {405, 298},   // 27
                {300, 332},      // 36, center
                {365, 421},   // 34
                {430, 511},   // 35
                {495, 600},     // 25
                {5000, 5000}
        };

        return createPath(coords);
    }

    private List<Position> createPathFrom10Center() {
        int[][] coords = {
                {5000, 5000},   // 0
                {529, 526},
                {543, 452},
                {567, 378},
                {591, 304},
                {615, 230},     // 5
                {552, 184},
                {489, 138},
                {426, 92},
                {363, 46},
                {300, 0},       // 10
                {300, 111},   // 28
                {300, 221},   // 29
                {300, 332},      // 36, center
                {365, 421},   // 34
                {430, 511},   // 35
                {495, 600},     // 25
                {5000, 5000}
        };

        return createPath(coords);
    }

    private List<Position> createPathFrom15Center() {
        int[][] coords = {
                {5000, 5000},   // 0
                {529, 526},
                {543, 452},
                {567, 378},
                {591, 304},
                {615, 230},     // 5
                {552, 184},
                {489, 138},
                {426, 92},
                {363, 46},
                {300, 0},       // 10
                {237, 46},
                {174, 92},
                {111, 138},
                {48, 184},
                {-15, 230},     // 15
                {90, 264},   // 30
                {195, 298},   // 31
                {365, 421},   // 34
                {430, 511},   // 35
                {300, 332},      // 36, center
                {495, 600},     // 25
                {5000, 5000}
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