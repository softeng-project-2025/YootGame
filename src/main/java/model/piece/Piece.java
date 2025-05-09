package model.piece;

import model.player.Player;
import model.position.Position;

import java.util.ArrayList;
import java.util.List;

public class Piece {
    private Player owner;
    private int id;
    private Position position;          // 현재 말이 있는 위치
    private Position startPosition;     // 말이 잡히면 돌아갈 위치
    private boolean isGrouped;
    private boolean isFinished;
    private boolean hasMoved = false;
    private boolean hasPassedCenter = false;
    private List<Piece> group = new ArrayList<>();
    private PathType pathType = PathType.OUTER;
    private List<Position> customPath = null;
    private int pathIndex = 0;


    public Piece(Player owner, int id, int playerNumber) {
        this.owner = owner;
        this.id = id;
        this.isGrouped = false;
        this.isFinished = false;
        this.group = new ArrayList<>(List.of(this));

        int x = 1200 - (4 - playerNumber) * 100;
        int y = 200 + id * 100;
        this.position = new Position(0, x, y);
        this.startPosition = this.position;
    }

    public Player getOwner() {
        return owner;
    }

    public int getId() {
        return id;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isGrouped() {
        return isGrouped;
    }

    public void setGrouped(boolean grouped) {
        this.isGrouped = grouped;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        this.isFinished = finished;
    }

    public List<Piece> getGroup() {
        return group;
    }

    public void setGroup(List<Piece> group) {
        this.group = group;
    }

    public void setMoved() {
        this.hasMoved = true;
    }

    public boolean hasMoved() {
        return this.hasMoved;
    }

    public boolean hasPassedCenter() {
        return hasPassedCenter;
    }

    public void setPassedCenter(boolean hasPassedCenter) {
        this.hasPassedCenter = hasPassedCenter;
    }

    public PathType getPathType() {
        return pathType;
    }
    public void setPathType(PathType pathType) {
        this.pathType = pathType;
    }

    public List<Position> getCustomPath() {
        return customPath;
    }

    public void setCustomPath(List<Position> path) {
        this.customPath = path;
        updatePathIndex(); // 초기화 후 바로 index 맞춤
    }

    public int getPathIndex() {
        return pathIndex;
    }

    public void advancePathIndex(int steps) {
        this.pathIndex += steps;
        if (this.customPath != null && this.pathIndex >= this.customPath.size()) {
            this.pathIndex = customPath.size() - 1;
        }
    }

    public void updatePathIndex() {
        if (customPath == null) return;
        for (int i = 0; i < customPath.size(); i++) {
            if (customPath.get(i).getIndex() == this.position.getIndex()) {
                this.pathIndex = i;
                return;
            }
        }
    }

    public void setPosition(Position position) {
        this.position = position;
        updatePathIndex(); // 위치 설정 시 pathIndex 자동 갱신
    }

    public void resetPath() {
        this.pathIndex = 0;
        this.customPath = null;
        this.pathType = PathType.OUTER;
        this.hasPassedCenter = false;
    }

    public void setPathIndex(int index) {
        this.pathIndex = index;
    }

}
