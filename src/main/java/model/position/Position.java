package model.position;

public class Position {
    private int index; // 경로상의 순서
    private int x, y;     // 화면상의 y 좌표
    private boolean isCenter; // 중심점 여부 (오각형, 육각형에서 활용)
    private boolean isDiagonalEntry;

    public Position(int index, int x, int y) {
        this(index, x, y, false, false);
    }

    public Position(int index, int x, int y, boolean isCenter) {
        this(index, x, y, isCenter, false);
    }

    public Position(int index, int x, int y, boolean isCenter, boolean isDiagonalEntry) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.isCenter = isCenter;
        this.isDiagonalEntry = isDiagonalEntry;
    }

    public boolean isCenter() {
        return isCenter;
    }

    public boolean isDiagonalEntry() { return isDiagonalEntry; }
    public int getIndex() { return index; }

    public void setCenter(boolean center) {
        isCenter = center;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
