package model.piece;

import model.Game;
import model.board.Board;
import model.player.Player;
import model.strategy.PathStrategy;
import model.yut.YutResult;

import java.util.ArrayList;
import java.util.List;

public class PieceUtil {

    public static List<Piece> getMovableGroup(Piece selected, Game game) {
        List<Piece> group = new ArrayList<>();
        for (Piece other : selected.getOwner().getPieces()) {
            if (!other.isFinished() &&
                    other.getPosition().equals(selected.getPosition()) &&
                    other.hasMoved()) {
                group.add(other);
            }
        }
        group.add(selected); // 항상 자신 추가
        ensureGroupConsistency(group);
        return group;
    }

    // 그룹 내 모든 Piece가 동일한 참조를 갖도록 설정
    public static void ensureGroupConsistency(List<Piece> group) {
        for (Piece p : group) {
            p.setGroup(group);
        }
    }

    // 단독 그룹으로 리셋 (잡힘, 완주)
    public static void resetGroupToSelf(Piece piece) {
        List<Piece> selfGroup = new ArrayList<>();
        selfGroup.add(piece);
        piece.setGroup(selfGroup);
    }

    public static boolean allFinished(List<Piece> group, Board board) {
        return group.stream().allMatch(p ->
                p.getPosition().equals(board.getPathStrategy().getPath().get(board.getPathStrategy().getPath().size() - 1))
        );
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

    public static List<Piece> getMovablePieces(Player player, YutResult result) {
        List<Piece> movable = new ArrayList<>();
        for (Piece piece : player.getPieces()) {
            if (!piece.isFinished() && canMove(piece, result)) {
                movable.add(piece);
            }
        }
        return movable;
    }

    private static boolean canMove(Piece piece, YutResult result) {
        if (result.getStep() > 0) {
            return true; // 앞으로는 무조건 가능
        } else {
            int newIndex = piece.getPathIndex() + result.getStep();
            return newIndex >= 0; // 빽도일 경우 되돌릴 수 있는지
        }
    }


}