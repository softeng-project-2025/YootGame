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
    private final int[] allVertexPositions = {
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
    private final int[] outerPath = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 44};
    private final int[] pathFrom5 = {0, 1, 2, 3, 4, 5, 31, 32, 43, 39, 40, 25, 26, 27, 28, 29, 30, 30, 44};
    private final int[] pathFrom10 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 33, 34, 43, 39, 40, 25, 26, 27, 28, 29, 30, 44};
    private final int[] pathFrom15 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 35, 36, 43, 39, 40, 25, 26, 27, 28, 29, 30, 44};
    private final int[] pathFrom20 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 37, 38, 43, 39, 40, 25, 26, 27, 28, 29, 30, 44};
    private final int[] pathFrom5Center = {0, 1, 2, 3, 4, 5, 31, 32, 43, 41, 42, 30, 44};
    private final int[] pathFrom10Center = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 33, 34, 43, 41, 42, 30, 44};
    private final int[] pathFrom15Center = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 35, 36, 43, 41, 42, 30, 44};
    private final int[] pathFrom20Center = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 37, 38, 43, 41, 42, 30, 44};

    public HexPathStrategy() {
        allPositions = createAllPositions();
    }

    @Override
    public Position getNextPosition(Piece piece, YutResult result) {
        PathType pathType = piece.getPathType();
        List<Position> path = piece.getCustomPath();
        int nextIndex = Math.min(piece.getPathIndex() + result.getStep(), path.size() - 1);

        // 모에 있었을 때 + 모든 5까지의 경로는 OUTER경로를 따르기에 조건 충분
        if (nextIndex == 5) {
            piece.setPathType(PathType.FROM5);
            piece.setCustomPath(createPath(pathFrom5));
            piece.setPathIndex(5);
        }

        // 뒷모에 있었을 때
        if (
                pathType == PathType.OUTER
                        && nextIndex == 10
        ) {
            piece.setPathType(PathType.FROM10);
            piece.setCustomPath(createPath(pathFrom10));
            piece.setPathIndex(10);
        }

        if (
                pathType == PathType.OUTER
                        && nextIndex == 15
        ) {
            piece.setPathType(PathType.FROM15);
            piece.setCustomPath(createPath(pathFrom15));
            piece.setPathIndex(15);
        }

        if (
                pathType == PathType.OUTER
                        && nextIndex == 20
        ) {
            piece.setPathType(PathType.FROM20);
            piece.setCustomPath(createPath(pathFrom20));
            piece.setPathIndex(20);
        }

        if (pathType == PathType.FROM5) {
            if (nextIndex == 8) {
                piece.setPathType(PathType.FROM5CENTER);
                piece.setCustomPath(createPath(pathFrom5Center));
                piece.setPathIndex(8);
            }
        }

        if (pathType == PathType.FROM10) {
            if (nextIndex == 13) {
                piece.setPathType(PathType.FROM10CENTER);
                piece.setCustomPath(createPath(pathFrom10Center));
                piece.setPathIndex(13);
            }
        }

        if (
                pathType == PathType.FROM15
                        && nextIndex == 18
        ) {
            piece.setPathType(PathType.FROM15CENTER);
            piece.setCustomPath(createPath(pathFrom15Center));
            piece.setPathIndex(18);
        }

        if (
                pathType == PathType.FROM20
                        && nextIndex == 23
        ) {
            piece.setPathType(PathType.FROM20CENTER);
            piece.setCustomPath(createPath(pathFrom20Center));
            piece.setPathIndex(23);
        }

        return path.get(nextIndex);
    }

    @Override
    public Position getPreviousPosition(Piece piece, YutResult result) {
        PathType pathType = piece.getPathType();
        int prevIndex = piece.getPathIndex() - 1;

        if (prevIndex == 0) {
            piece.setPathType(PathType.OUTER);
            piece.setCustomPath(createPath(outerPath));
            piece.setPathIndex(30);
            prevIndex = 30;
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

        if (
                pathType == PathType.FROM15
                        && prevIndex == 14
        ) {
            piece.setPathType(PathType.OUTER);
            piece.setCustomPath(createPath(outerPath));
            piece.setPathIndex(14);
        }

        if (
                pathType == PathType.FROM20
                        && prevIndex == 19
        ) {
            piece.setPathType(PathType.OUTER);
            piece.setCustomPath(createPath(outerPath));
            piece.setPathIndex(19);
        }

        if (
                pathType == PathType.FROM5
                        && prevIndex == 7
        ) {
            piece.setPathType(PathType.FROM5);
            piece.setCustomPath(createPath(pathFrom5));
            piece.setPathIndex(7);
        }

        if (
                pathType == PathType.FROM10
                        && prevIndex == 12
        ) {
            piece.setPathType(PathType.FROM10);
            piece.setCustomPath(createPath(pathFrom10));
            piece.setPathIndex(12);
        }

        if (
                pathType == PathType.FROM15
                        && prevIndex == 17
        ) {
            piece.setPathType(PathType.FROM15);
            piece.setCustomPath(createPath(pathFrom15));
            piece.setPathIndex(17);
        }

        if (
                pathType == PathType.FROM20
                        && prevIndex == 22
        ) {
            piece.setPathType(PathType.FROM20);
            piece.setCustomPath(createPath(pathFrom20));
            piece.setPathIndex(22);
        }

        List<Position> path = piece.getCustomPath();
        return path.get(prevIndex);
    }

    @Override
    public java.util.List<Position> getPath() {
        return createPath(outerPath);
    }

    public List<Position> getAllPositions() {
        return allPositions;
    }

    @Override
    public List<Position> getAllVertexPositions() {
        return createPath(allVertexPositions);
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

    private List<Position> createPath(int[] coords) {
        List<model.position.Position> path = new ArrayList<>();
        for (int i : coords) {
            path.add(allPositions.get(i));
        }
        return path;
    }
}