package model.piece;

import model.player.Player;
import model.position.Position;
import java.util.List;
import java.util.Objects;

// 말 하나의 상태(위치, 경로, 이동 여부 등)만을 관리합니다.
public class Piece {
    private final Player owner;
    private final int id;
    private Position position;
    private final Position startPosition;
    private boolean finished;
    private boolean moved;
    private boolean passedCenter;
    private PathType pathType;
    private List<Position> customPath;
    private int pathIndex;

    // 생성자: 시작 위치는 외부에서 계산하여 전달합니다.
    public Piece(Player owner, int id, Position startPosition) {
        this.owner = Objects.requireNonNull(owner);
        this.id = id;
        this.startPosition = Objects.requireNonNull(startPosition);
        this.position = startPosition;
        this.pathType = PathType.OUTER;
        this.customPath = null;
        this.pathIndex = 0;
        this.finished = false;
        this.moved = false;
        this.passedCenter = false;
    }


    public Player getOwner() {
        return owner;
    }

    public int getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean hasMoved() {
        return moved;
    }

    public boolean hasPassedCenter() {
        return passedCenter;
    }

    public PathType getPathType() {
        return pathType;
    }

    public List<Position> getCustomPath() {
        return customPath;
    }

    public int getPathIndex() {
        return pathIndex;
    }

    // 사용자 정의 경로 설정
    public void setCustomPath(List<Position> customPath) {
        this.customPath = Objects.requireNonNull(customPath);
        this.pathIndex = findIndexForPosition(this.position); // 초기화 후 바로 index 맞춤
    }

    // 현재 위치에 대응하는 customPath 인덱스를 찾습니다.
    private int findIndexForPosition(Position pos) {
        for (int i = 0; i < customPath.size(); i++) {
            if (customPath.get(i).index() == pos.index()) {
                return i;
            }
        }
        return 0;
    }

    // 한 턴 이동: 새로운 위치와 스텝 수로 상태 업데이트
    public void moveTo(Position newPosition, int step) {
        Objects.requireNonNull(newPosition);
        this.position = newPosition;
        if (customPath != null) {
            this.pathIndex = findIndexForPosition(newPosition);
            if (pathIndex >= customPath.size()) {
                this.pathIndex = customPath.size() - 1;
            }
            if (pathIndex == customPath.size() - 1) { // 경로 끝 도달 시 완료 처리
                this.finished = true;
            }
        } else {
            this.pathIndex += step;
        }
        this.moved = true;
        if (newPosition.isCenter()) {
            this.passedCenter = true;
        }
    }

    // 다음 턴을 위해 상태 초기화
    public void resetForNextTurn() {
        this.moved = false;
    }

    public void resetToStart() {
        this.position = startPosition;
        this.finished = false;
        this.moved = false;

        this.passedCenter = false;
        this.pathType = PathType.OUTER;
        this.customPath = null;
        this.pathIndex = 0;
    }

}
