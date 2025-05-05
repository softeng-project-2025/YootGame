package model.piece;

import model.strategy.PathStrategy;

import java.util.List;

public class PieceUtil {

    // 그룹 내 모든 Piece가 동일한 참조를 갖도록 설정
    public static void ensureGroupConsistency(List<Piece> group) {
        for (Piece p : group) {
            p.setGroup(group);
        }
    }

    // 단독 그룹으로 리셋 (잡힘, 완주)
    public static void resetGroupToSelf(Piece p) {
        p.setGroup(List.of(p));
    }

    public static void initializePath(Piece piece, PathStrategy strategy) {
        piece.setPathType(PathType.OUTER);
        piece.setCustomPath(strategy.getPath());
        piece.setPathIndex(0);
    }

    public static void resetPieceState(Piece piece, PathStrategy pathStrategy) {
        piece.setPosition(pathStrategy.getPath().get(0)); // 출발점
        piece.resetPath(); // customPath, index, pathType, hasPassedCenter 초기화
        piece.setFinished(false);
        resetGroupToSelf(piece); // 그룹도 초기화
    }
}