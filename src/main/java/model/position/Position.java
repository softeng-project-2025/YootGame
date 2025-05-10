package model.position;

public record Position(
        int index, // 경로상의 순서
        int x,
        int y,
        boolean isCenter, // 중심점 여부
        boolean isDiagonalEntry
) {
    // 기본 생성자: 중심, 대각선 입구 플래그는 false로 설정
    public Position(int index, int x, int y) {
        this(index, x, y, false, false);
    }

    // 중심 플래그만 설정하는 생성자
    public Position(int index, int x, int y, boolean isCenter) {
        this(index, x, y, isCenter, false);
    }
}

