package model.strategy;

import model.piece.PathType;
import model.piece.Piece;
import model.position.Position;
import model.yut.YutResult;

import java.util.ArrayList;
import java.util.List;

/**
 * All indexes of SquarePathStrategy follow under figure.
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠ 10 ⣤⡀
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⡴⠛⠁⠀⣹⠄⠀ 9
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀ 11⠉⠀⠀⠀⠀⢼⠂⠀⠀⠀⠈⠻⢦⡀
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠12⠁⠀⠀⠀⠀⠀⠀ 28⠀⠀⠀⠀⠀⠀  8⡀
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣤13⠀⠀⠀⠀⠀⠀⠀⠀⠀⣹⠄⠀⠀⠀⠀⠀⠀⠀⠀⠈⠛ 7
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ 14 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢼⠂⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠳⣆⡀
 ⠀⠀⠀⠀⠀⠀⠀⣀⡴⠛⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ 29⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠙ 6
 ⠀⠀⠀⠀⠀⠀⠀15 ⡛ 30 ⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣹⠄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ 26 ⠚⢋ 5
 ⠀⠀⠀⠀⠀⠀⠀⠘⣧⠀⠀⠀⠈⠉⠛ 31 ⣄⣀⠀⠀⠀⠀⠀  ⠀⠀⠀⠀  27 ⠞⠛ ⠀⠀⠀⠀⣼⠃
 ⠀⠀⠀⠀⠀⠀⠀⠀⢸⡆⠀⠀⠀⠀⠀⠀⠀⠀⠈⠉⠙⠳⠳  36 ⣠⡴⠖⠛⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀ 4
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀16⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀     ⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⠁
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣧⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡾⠁⠀⠈⢳⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ 3
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀17⠀⠀⠀⠀⠀⠀⠀⠀⠀32⠀⠀⠀⠀⠀ 34⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡿
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⣷⠀⠀⠀⠀⠀⠀⠀⢀⡼⠃⠀⠀⠀⠀⠀⠀⠀⠈⢷⡀⠀⠀⠀⠀⠀⠀⠀ 2
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀18⠀⠀⠀⠀⠀⢠⡞⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠻⣄⠀⠀⠀⠀⠀⢠⡏
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢻⡀⠀⠀⠀33⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ 35⠀⠀  1
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈19⠀⢀⡼⠃⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⢳⣄⠀⣸⠃
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⣦⠟⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⣦⡟
 ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀20⠋⠋ 21 ⠙ 22 ⠙ 23⠛ 24⠋⠛ 25
 */
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
        List<Position> path = piece.getCustomPath();
        int pathIndex = piece.getPathIndex();
        int nextIndex = pathIndex + result.getStep();
        if (nextIndex >= path.size()) {
            nextIndex = path.size() - 1;
        }

        // 모에 있었을 때 + 5까지는 모두 OUTER를 따르기에 조건 충분
        if (nextIndex == 5) {
            piece.setPathType(PathType.FROM5);
            piece.setCustomPath(pathFrom5);
            piece.setPathIndex(5);
        }

        // 뒷모에 있었을 때 + 외곽 경로를 따라와서 모서리로 도착했을 때에만
        if (
                pathType == PathType.OUTER
                        && nextIndex == 10
        ) {
            piece.setPathType(PathType.FROM10);
            piece.setCustomPath(pathFrom10);
            piece.setPathIndex(10);
        }

        // "외곽 경로를 따라와서 모서리에 도착했을 때에만" 조건 추간
        if (
                pathType == PathType.OUTER
                        && nextIndex == 15
        ) {
            piece.setPathType(PathType.FROM15);
            piece.setCustomPath(pathFrom15);
            piece.setPathIndex(15);
        }

        if (pathType == PathType.FROM5) {
            if (nextIndex == 8) {
                piece.setPathType(PathType.FROM5CENTER);
                piece.setCustomPath(pathFrom5Center);
                piece.setPathIndex(8);
            }
        }
        else if (pathType == PathType.FROM10) {
            if (nextIndex == 13) {
                piece.setPathType(PathType.FROM10CENTER);
                piece.setCustomPath(pathFrom10Center);
                piece.setPathIndex(13);
            }
        }
        else if (pathType == PathType.FROM15) {
            if (nextIndex == 18) {
                piece.setPathType(PathType.FROM15CENTER);
                piece.setCustomPath(pathFrom15Center);
                piece.setPathIndex(18);
            }
        }

        return path.get(nextIndex);
    }

    @Override
    public Position getPreviousPosition(Piece piece, YutResult result) {
        PathType pathType = piece.getPathType();
        int pathIndex = piece.getPathIndex();
        int prevIndex = pathIndex - 1;

        if (pathIndex == 1) {
            piece.setPathType(PathType.OUTER);
            piece.setCustomPath(outerPath);
            piece.setPathIndex(25);
            prevIndex = 25;
        }

        // 모에 있었다가 백도 받아서 윷에 있었을 때
        if (
                pathType == PathType.FROM5
                        && prevIndex == 4
        ) {
            piece.setPathType(PathType.OUTER);
            piece.setCustomPath(outerPath);
            piece.setPathIndex(4);
        }

        // 뒷모에 있었다가 백도를 받아서 뒷윷에 있었을 때
        if (
                pathType == PathType.FROM10
                        && prevIndex == 9
        ) {
            piece.setPathType(PathType.OUTER);
            piece.setCustomPath(outerPath);
            piece.setPathIndex(9);
        }

        if (
                pathType == PathType.FROM15
                        && prevIndex == 14
        ) {
            piece.setPathType(PathType.OUTER);
            piece.setCustomPath(outerPath);
            piece.setPathIndex(14);
        }

        if (
                pathType == PathType.FROM5
                        && prevIndex == 7
        ) {
            piece.setPathType(PathType.FROM5);
            piece.setCustomPath(pathFrom5);
            piece.setPathIndex(7);
        }

        if (
                pathType == PathType.FROM10
                        && prevIndex == 12
        ) {
            piece.setPathType(PathType.FROM10);
            piece.setCustomPath(pathFrom10);
            piece.setPathIndex(12);
        }

        if (
                pathType == PathType.FROM15
                        && prevIndex == 17
        ) {
            piece.setPathType(PathType.FROM15);
            piece.setCustomPath(pathFrom15);
            piece.setPathIndex(17);
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
                {519, 526},
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
                {5000, 5000}
        };

        for (int i = 0; i < allCoords.length; i++) {
            int x = allCoords[i][0];
            int y = allCoords[i][1];
            boolean isVertex = (i <= 25 && i % 5 == 0);
            boolean isCenter = (i == allCoords.length - 2);
            positions.add(new Position(i, x, y, isCenter, isVertex));
        }
        return positions;
    }

    // for drawing board's background line
    private List<Position> createAllVertexPositions() {
        int[] coords = {
                // outer lines
                5, 10,
                10, 15,
                15, 20,
                20, 25,
                25, 5,
                // inner lines
                36, 5,
                36, 10,
                36, 15,
                36, 20,
                36, 25
        };
        return createPath(coords);
    }

    private List<Position> createOuterPath() {
        int[] coords = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 37};
        return createPath(coords);
    }

    private List<Position> createPathFrom5() {
        int[] coords = {0, 1, 2, 3, 4, 5, 26, 27, 36, 32, 33, 20, 21, 22, 23, 24, 25, 37};
        return createPath(coords);
    }

    private List<Position> createPathFrom10() {
        int[] coords = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 28, 29, 36, 32, 33, 20, 21, 22, 23, 24, 25, 37};
        return createPath(coords);
    }

    private List<Position> createPathFrom15() {
        int[] coords = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 30, 31, 36, 32, 33, 20, 21, 22, 23, 24, 25, 37};
        return createPath(coords);
    }

    private List<Position> createPathFrom5Center() {
        int[] coords = {0, 1, 2, 3, 4, 5, 26, 27, 36, 34, 35, 25, 37};
        return createPath(coords);
    }

    private List<Position> createPathFrom10Center() {
        int[] coords = {0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 28, 29, 36, 34, 35, 25, 37};
        return createPath(coords);
    }

    private List<Position> createPathFrom15Center() {
        int[] coords = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 30, 31, 36, 34, 35, 25, 37};
        return createPath(coords);
    }

    private List<Position> createPath(int[] coords) {
        List<Position> path = new ArrayList<>();
        for (int i : coords) {
            path.add(allPositions.get(i));
        }
        return path;
    }
}