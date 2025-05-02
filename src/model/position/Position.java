package model.position;

public class Position {
    private int index; // 경로상의 순서
    private int x;     // 화면상의 x 좌표
    private int y;     // 화면상의 y 좌표
    private boolean isCenter; // 중심점 여부 (오각형, 육각형에서 활용)

    public Position(int index, int x, int y) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.isCenter = false;
    }

    public Position(int index, int x, int y, boolean isCenter) {
        this(index, x, y);
        this.isCenter = isCenter;
    }

    public int getIndex() {
        return index;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isCenter() {
        return isCenter;
    }

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
}
