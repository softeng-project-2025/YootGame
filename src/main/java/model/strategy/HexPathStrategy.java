package model.strategy;

import model.piece.PathType;
import model.piece.Piece;
import model.position.Position;
import model.yut.YutResult;

import java.util.ArrayList;
import java.util.List;

/**
 * All indexes of SquarePathStrategy follow under figure.
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀15 ⢤⠤ 14⠤⡤ 13⢤ 12⠤ 11⡤ 10
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡰⠃⠳⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠼⠙⢦
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀16 ⠀⠀⠹⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡞⠁⠀ 9
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡜⠀⠀⠀⠀⠀⠙⣆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠏⠀⠀⠀⠀⠀⢳⡀
 ⠀⠀⠀⠀⠀⠀⠀⠀ 17⠀⠀⠀⠀⠀⠀⠀ 35⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀33 ⠀⠀⠀⠀⠀⠀ 8
 ⠀⠀⠀⠀⠀⠀⠀⢠⠎⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⢧⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣰⠃⠀ ⠀⠀⠀⠀⠀⠀⠀⠀⠹⣄
 ⠀⠀⠀⠀⠀⠀ 18⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⢳⡀⠀⠀⠀⠀⠀⠀⠀⡼⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ 7
 ⠀⠀⠀⠀⠀⡰⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ 36⠀⠀⠀⠀  34⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⢧
 ⠀⠀⠀⠀ 19⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠹⡄⠀⠀⢀⡞⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ 6
 ⠀⠀⢀⡜⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⣆⢠⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠳
 ⠀⠀20 ⠲⠲⠲ 39 ⠲⠲⠲⠲ 38 ⠲⠲⠲ 43 ⠲⢺   32⠒⠒   31     5
 ⠀⠀⠀⠳⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣰⠃⠈⢧⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ 4⠀
 ⠀⠀⠀⠀ 21⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡰⠁⠀⠀⠈⢳⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠞⠀
 ⠀⠀⠀⠀⠀⠘⣆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ 39 ⠀⠀⠀⠀⠀ 41 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ 3⠀
 ⠀⠀⠀⠀⠀⠀ 22⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡞⠀⠀⠀⠀⠀⠀⠀⠀⠹⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣰⠃
 ⠀⠀⠀⠀⠀⠀⠀⠈⢧⡀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠞⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⣆⠀⠀⠀⠀⠀⠀⠀⠀⠀ 2
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀ 23⠀⠀⠀⠀⠀⠀ 40⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ 42⠀⠀⠀⠀⠀⠀⢀⡼⠁
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠹⡄⠀⠀⠀⠀⣰⠃⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⢧⠀⠀⠀⠀  1
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ 24⠀⠀⡴⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⢳⡀⠀⢠⠏
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀  25⢁⢀ 26 ⢀27⡀ 28⢀⠀29⠀⡀ 30
 */
public class HexPathStrategy implements PathStrategy {

    private final List<Position> allPositions;
    private final List<Position> allVertexPositions;
    private final List<Position> outerPath;
    private final List<Position> pathFrom5;
    private final List<Position> pathFrom10;
    private final List<Position> pathFrom15;
    private final List<Position> pathFrom20;
    private final List<Position> pathFrom5Center;
    private final List<Position> pathFrom10Center;
    private final List<Position> pathFrom15Center;
    private final List<Position> pathFrom20Center;

    public HexPathStrategy() {
        allPositions = createAllPositions();
        allVertexPositions = createAllVertexPositions();
        outerPath = createOuterPath();
        pathFrom5 = createPathFrom5();
        pathFrom10 = createPathFrom10();
        pathFrom15 = createPathFrom15();
        pathFrom20 = createPathFrom20();
        pathFrom5Center = createPathFrom5Center();
        pathFrom10Center = createPathFrom10Center();
        pathFrom15Center = createPathFrom15Center();
        pathFrom20Center = createPathFrom20Center();
    }

    @Override
    public Position getNextPosition(Piece piece, YutResult result) {
        PathType pathType = piece.getPathType();
        int pathIndex = piece.getPathIndex();

        // 모에 있었을 때 + 모든 5까지의 경로는 OUTER경로를 따르기에 조건 충분
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
        if (pathIndex == 10 && pathType == PathType.OUTER) {
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

        if (pathIndex == 15 && pathType == PathType.OUTER) {
            piece.setPathType(PathType.FROM15);
            piece.setCustomPath(pathFrom15);
            piece.setPathIndex(15);
        }

        if (
                pathType == PathType.FROM15
                        && pathIndex == 14
        ) {
            piece.setPathType(PathType.OUTER);
            piece.setCustomPath(outerPath);
            piece.setPathIndex(14);
        }

        if (pathIndex == 20 && pathType == PathType.OUTER) {
            piece.setPathType(PathType.FROM20);
            piece.setCustomPath(pathFrom20);
            piece.setPathIndex(20);
        }

        if (
                pathType == PathType.FROM20
                        && pathIndex == 19
        ) {
            piece.setPathType(PathType.OUTER);
            piece.setCustomPath(outerPath);
            piece.setPathIndex(19);
        }

        if (pathType == PathType.FROM5) {
            if (pathIndex == 8) {
                piece.setPathType(PathType.FROM5CENTER);
                piece.setCustomPath(pathFrom5Center);
                piece.setPathIndex(8);
            }
            else if (pathIndex == 7) {
                piece.setPathType(PathType.FROM5);
                piece.setCustomPath(pathFrom5);
                piece.setPathIndex(7);
            }
        }
        else if (pathType == PathType.FROM10) {
            if (pathIndex == 13) {
                piece.setPathType(PathType.FROM10CENTER);
                piece.setCustomPath(pathFrom10Center);
                piece.setPathIndex(13);
            }
            else if (pathIndex == 12) {
                piece.setPathType(PathType.FROM10);
                piece.setCustomPath(pathFrom10);
                piece.setPathIndex(12);
            }
        }
        else if (pathType == PathType.FROM15) {
            if (pathIndex == 18) {
                piece.setPathType(PathType.FROM15CENTER);
                piece.setCustomPath(pathFrom15Center);
                piece.setPathIndex(18);
            }
            else if (pathIndex == 17) {
                piece.setPathType(PathType.FROM15);
                piece.setCustomPath(pathFrom15);
                piece.setPathIndex(17);
            }
        }
        else if (pathType == PathType.FROM20) {
            if (pathIndex == 23) {
                piece.setPathType(PathType.FROM20CENTER);
                piece.setCustomPath(pathFrom20Center);
                piece.setPathIndex(23);
            }
            else if (pathIndex == 22) {
                piece.setPathType(PathType.FROM20);
                piece.setCustomPath(pathFrom20);
                piece.setPathIndex(22);
            }
        }

        List<Position> path = piece.getCustomPath();
        pathIndex = piece.getPathIndex();
        int nextIdx = pathIndex + result.getStep();
        if (nextIdx >= path.size()) {
            nextIdx = path.size() - 1;
        }

        return path.get(nextIdx);
    }

    @Override
    public Position getPreviousPosition(Piece piece, YutResult result) {
        int pathIndex = piece.getPathIndex();
        int prevIndex = pathIndex - 1;

        if (pathIndex == 1) {
            piece.setPathType(PathType.OUTER);
            piece.setCustomPath(outerPath);
            piece.setPathIndex(30);
            prevIndex = 30;
        }

        List<Position> path = piece.getCustomPath();
        return path.get(prevIndex);
    }

    @Override
    public java.util.List<Position> getPath() {
        return outerPath;
    }

    public List<Position> getAllPositions() {
        return allPositions;
    }

    @Override
    public List<Position> getAllVertexPositions() {
        return allVertexPositions;
    }

    private List<Position> createAllPositions() {
        List<Position> positions = new ArrayList<>();
        int [][] allCoords = {
                {5000, 5000},
                {555, 540},
                {590, 480},
                {625, 420},
                {660, 360},
                {695, 300},   // 5
                {660, 240},
                {625, 180},
                {590, 120},
                {555, 60},
                {520, 0},       // 10
                {451, 0},
                {382, 0},
                {313, 0},
                {244, 0},
                {175, 0},   // 15
                {140, 60},
                {105, 120},
                {70, 180},
                {35, 240},
                {0, 300},   // 20
                {35, 360},
                {70, 420},
                {105, 480},
                {140, 540},
                {175, 600},   //25
                {244, 600},
                {313, 600},
                {382, 600},
                {451, 600},
                {520, 600},   // 30
                {579, 300}, //31
                {463, 300}, //32
                {463, 100}, //33
                {405, 200}, //34
                {233, 100}, //35
                {290, 200}, //36
                {116, 300},//37
                {232, 300},//38
                {290, 400}, //39
                {233, 500}, //40
                {405, 400},//41
                {463, 500},//42
                {348, 300}, //43, center
                {5000, 5000},
        };

        for (int i = 0; i < allCoords.length; i++) {
            int x = allCoords[i][0];
            int y = allCoords[i][1];
            boolean isVertex = (i <= 30 && i % 5 == 0);
            boolean isCenter = (i == allCoords.length - 2);
            positions.add(new Position(i, x, y, isCenter, isVertex));
        }

        return positions;
    }

    // for drawing board's background line
    private List<Position> createAllVertexPositions() {
        int [] coords = {
                // outer lines
                5, 10,
                10, 15,
                15, 20,
                20, 25,
                25, 30,
                30, 5,
                // inner lines
                5, 20,
                10, 25,
                15, 30
        };
        return createPath(coords);
    }

    private List<Position> createOuterPath() {
        int[] coords = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 44};
        return createPath(coords);
    }

    private List<Position> createPathFrom5() {
        int[] coords = {0, 1, 2, 3, 4, 5, 31, 32, 43, 39, 40, 25, 26, 27, 28, 29, 30, 30, 44};
        return createPath(coords);
    }

    private List<Position> createPathFrom10() {
        int[] coords = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 33, 34, 43, 39, 40, 25, 26, 27, 28, 29, 30, 44};
        return createPath(coords);
    }

    private List<Position> createPathFrom15() {
        int[] coords = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 35, 36, 43, 39, 40, 25, 26, 27, 28, 29, 30, 44};
        return createPath(coords);
    }

    private List<Position> createPathFrom20() {
        int[] coords = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 37, 38, 43, 39, 40, 25, 26, 27, 28, 29, 30, 44};
        return createPath(coords);
    }

    private List<Position> createPathFrom5Center() {
        int[] coords = {0, 1, 2, 3, 4, 5, 31, 32, 43, 41, 42, 30, 44};
        return createPath(coords);
    }

    private List<Position> createPathFrom10Center() {
        int[] coords = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 33, 34, 43, 41, 42, 30, 44};
        return createPath(coords);
    }

    private List<Position> createPathFrom15Center() {
        int[] coords = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 35, 36, 43, 41, 42, 30, 44};
        return createPath(coords);
    }

    private List<Position> createPathFrom20Center() {
        int[] coords = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 37, 38, 43, 41, 42, 30, 44};
        return createPath(coords);
    }

    private List<Position> createPath(int[] coords) {
        List<model.position.Position> path = new ArrayList<>();
        for (int i : coords) {
            path.add(allPositions.get(i));
        }
        return path;
    }
}