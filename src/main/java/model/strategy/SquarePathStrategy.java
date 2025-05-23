package model.strategy;

import model.piece.Piece;
import model.position.Position;
import model.yut.YutResult;

import java.util.ArrayList;
import java.util.List;
import model.piece.PathType;

/**
 * All indexes of SquarePathStrategy follow under figure.
 *⠀⠀⠀⠀10⡶⠲⠲⠲ 9⠲⠲⠲ 8⠲⠲⠲⠲ 7 ⠲⠲ 6⠲⠲⠲ 5
 *⠀⠀⠀⠀⢸⡇⠙⢦⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣤⠟⠁⣿
 *⠀⠀⠀⠀⢸⡇⠀⠀ 23⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀  21⠀⠀⠀⣿
 *⠀⠀⠀⠀ 11⠀⠀⠀⠀⠙⢦⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡤⠟⠁⠀⠀⠀⠀4
 *⠀⠀⠀⠀⢸⡇⠀⠀⠀⠀⠀⠀⠙⢦⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣰⠏⠁⠀⠀⠀⠀⠀⠀⣿
 *⠀⠀⠀⠀⢸⡇⠀⠀⠀⠀⠀⠀⠀⠀ 24 ⠀⠀⠀⠀⠀⠀⠀22⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿
 *⠀⠀⠀⠀ 12⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⢧⡀⠀⠀⠀⣴⠟⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀3
 *⠀⠀⠀⠀⢸⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠛⢧⣴⠟⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⠀
 *⠀⠀⠀⠀⢸⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣤ 29 ⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿
 *⠀⠀⠀⠀ 13 ⠀⠀⠀⠀⠀⠀⠀⠀⢀⣠⠞⠁⠀⠀⠀⠛⣦⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀2
 *⠀⠀⠀⠀⢸⡇⠀⠀⠀⠀⠀⠀⠀  25⠀⠀⠀⠀⠀⠀⠀ 27 ⠀⠀⠀⠀⠀⠀⠀⠀⣿
 *⠀⠀⠀⠀⢸⡇⠀⠀⠀⠀⠀⠀⣰⠞⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠹⢦⡀⠀⠀⠀⠀⠀⠀⣿
 *⠀⠀⠀⠀ 14⠀⠀⠀⠀⣠⠟⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠙⣦⡀⠀⠀⠀⠀1
 *⠀⠀⠀⠀⢸⡇⠀⠀ 26⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ 28 ⠀⠀⣿
 *⠀⠀⠀⠀⢸⡇⣰⠞⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠙⢦⡀⣿
 *⠀⠀⠀⠀15⢥⣤⢤ 16 ⣤ 17 ⢤⣤ 18 ⢤⣤ 19 ⣤⢤ 20
 */
public class SquarePathStrategy implements PathStrategy {

    private final List<Position> allPositions;
    private final int[] allVertexPositions = {
            // outer lines
            5, 10,
            10, 15,
            15, 20,
            20, 5,
            // inner lines
            5, 15,
            10, 20
    };
    private final int[] outerPath = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 30};
    private final int[] pathFrom5 = {0, 1, 2, 3, 4, 5, 21, 22, 29, 25, 26, 15, 16, 17, 18, 19, 20, 30};
    private final int[] pathFrom10 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 23, 24, 29, 27, 28, 20, 30};
    private final int[] pathFrom5Center = {0, 1, 2, 3, 4, 5, 21, 22, 29, 27, 28, 20, 30};

    public SquarePathStrategy() {
        allPositions = createAllPositions();
    }

    // 경로 업데이트 & 업데이트된 경로 기반 다음 위치
    @Override
    public Position getNextPosition(Piece piece, YutResult result) {
        PathType pathType = piece.getPathType();
        List<Position> path = piece.getCustomPath();
        int nextIndex = Math.min(piece.getPathIndex() + result.getStep(), path.size() - 1);

        // 모에 있었을 때 + 5번째까지는 모든 경로가 OUTER를 따르기에 조건 충분
        if (nextIndex == 5) {
            piece.setPathType(PathType.FROM5);
            piece.setCustomPath(createPath(pathFrom5));
            piece.setPathIndex(5);
        }

        // 뒷모에 있었을 때 + 외곽 경로에서 왔을 때만 인정
        if (
                pathType == PathType.OUTER
                        && nextIndex == 10
        ) {
            piece.setPathType(PathType.FROM10);
            piece.setCustomPath(createPath(pathFrom10));
            piece.setPathIndex(10);
        }

        // (모도, 모개 || 속윷에 있다가 빽도)로 와서 방에 있었을 때
        if (
                pathType == PathType.FROM5
                        && nextIndex == 8
        ) {
            piece.setPathType(PathType.FROM5CENTER);
            piece.setCustomPath(createPath(pathFrom5Center));
            piece.setPathIndex(8);
        }

        return path.get(nextIndex);
    }

    @Override
    public Position getPreviousPosition(Piece piece, YutResult result) {
        int prevIndex = piece.getPathIndex() - 1;
        PathType pathType = piece.getPathType();

        if (prevIndex == 0) {
            piece.setPathType(PathType.OUTER);
            piece.setCustomPath(createPath(outerPath));
            piece.setPathIndex(20);
            prevIndex = 20;
        }

        // 모에 있었다가 백도 받아서 윷에 있었을 때
        if (
                pathType == PathType.FROM5
                        && prevIndex == 4
        ) {
            piece.setPathType(PathType.OUTER);
            piece.setCustomPath(createPath(outerPath));
            piece.setPathIndex(4);
        }

        // 뒷모에 있었다가 백도를 받아서 뒷윷에 있었을 때
        if (
                pathType == PathType.FROM10
                        && prevIndex == 9
        ) {
            piece.setPathType(PathType.OUTER);
            piece.setCustomPath(createPath(outerPath));
            piece.setPathIndex(9);
        }

        // (모도, 모개 || 속윷에 있다가 빽도)로 와서 방에 있었을 때
        if (
                pathType == PathType.FROM5
                        && prevIndex == 8
        ) {
            piece.setPathType(PathType.FROM5CENTER);
            piece.setCustomPath(createPath(pathFrom5Center));
            piece.setPathIndex(8);
        }

        // from5이면서 방 들어갔다가 빽도로 나오면 pathFrom5로 변경
        if (
                pathType == PathType.FROM5CENTER
                        && prevIndex == 7
        ) {
            piece.setPathType(PathType.FROM5);
            piece.setCustomPath(createPath(pathFrom5));
            piece.setPathIndex(7);
        }

        List<Position> path = piece.getCustomPath();
        return path.get(prevIndex);
    }

    @Override
    public List<Position> getPath() {
        return createPath(outerPath);
    }

    @Override
    public List<Position> getAllPositions() {
        return allPositions;
    }

    @Override
    public List<Position> getAllVertexPositions() {
        return createPath(allVertexPositions);
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
                {480, 600},
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
                {5000, 5000}
        };

        for (int i = 0; i < allCoords.length; i++) {
            int x = allCoords[i][0];
            int y = allCoords[i][1];
            boolean isVertex = (i <= 20 && i % 5 == 0);
            boolean isCenter = (i == allCoords.length - 2);
            positions.add(new Position(i, x, y, isCenter, isVertex));
        }
        return positions;
    }

    private List<Position> createPath(int[] coords) {
        List<Position> path = new ArrayList<>();
        for (int i : coords) {
            path.add(allPositions.get(i));
        }
        return path;
    }

}