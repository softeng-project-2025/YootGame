package model.piece;

import model.Game;
import model.board.Board;
import model.player.Player;
import model.position.Position;
import model.strategy.PathStrategy;
import model.yut.YutResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Piece 관련 유틸리티: 그룹핑, 이동 가능 여부, 초기화 로직 등
 */
public final class PieceUtil {

    private PieceUtil() { /* 유틸 클래스 방지 */ }


//    /**
//     * 말의 초기 경로와 상태를 설정합니다.
//     */
//    public static void initializePath(Piece piece, PathStrategy strategy) {
//        Objects.requireNonNull(piece);
//        Objects.requireNonNull(strategy);
//        piece.setPathType(PathType.OUTER);
//        piece.setCustomPath(new ArrayList<>(strategy.getPath()));
//        piece.setPathIndex(0);
//    }

    /**
     * 플레이어의 말 중 주어진 윷 결과로 움직일 수 있는 말만 반환합니다.
     */
    public static List<Piece> getMovablePieces(Player player, YutResult result) {
        return player.getPieces().stream()
                .filter(p -> !p.isFinished() && canMove(p, result))
                .collect(Collectors.toList());
    }

    /**
     * 윷 결과에 따라 말이 이동 가능한지 판단합니다.
     */
    private static boolean canMove(Piece piece, YutResult result) {
        int step = result.getStep();
        if (step > 0) return true;
        int newIndex = piece.getPathIndex() + step;
        return newIndex >= 0;
    }
}
