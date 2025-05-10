package model.strategy;

import model.piece.PathType;
import model.piece.Piece;
import model.position.Position;
import model.yut.YutResult;

import java.util.ArrayList;
import java.util.List;


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

        if (pathIndex == 15) {
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

        if (pathIndex == 20) {
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
        int [][] allCoords = {

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

        };

        return createPath(coords);
    }

    private List<Position> createPathFrom5() {
        int[][] coords = {

        };

        return createPath(coords);
    }

    private List<Position> createPathFrom10() {
        int[][] coords = {

        };

        return createPath(coords);
    }

    private List<Position> createPathFrom15() {
        int[][] coords = {

        };

        return createPath(coords);
    }

    private List<Position> createPathFrom20() {
        int[][] coords = {

        };

        return createPath(coords);
    }

    private List<Position> createPathFrom5Center() {
        int[][] coords = {

        };

        return createPath(coords);
    }

    private List<Position> createPathFrom10Center() {
        int[][] coords = {

        };

        return createPath(coords);
    }

    private List<Position> createPathFrom15Center() {
        int[][] coords = {

        };

        return createPath(coords);
    }

    private List<Position> createPathFrom20Center() {
        int[][] coords = {

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