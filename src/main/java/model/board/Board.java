package model.board;

import model.piece.Piece;
import model.piece.PieceUtil;
import model.player.Player;
import model.position.Position;
import model.yut.YutResult;
import model.strategy.PathStrategy;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private PathStrategy pathStrategy;

    public Board(PathStrategy pathStrategy) {
        this.pathStrategy = pathStrategy;
    }

    // Piece가 현재 위치에서 YutResult에 따라 다음 위치로 이동
    public Position getNextPosition(Piece piece, YutResult result) {
        return pathStrategy.getNextPosition(piece, result);
    }

    public PathStrategy getPathStrategy() {
        return pathStrategy;
    }

    public void setPathStrategy(PathStrategy pathStrategy) {
        this.pathStrategy = pathStrategy;
    }

    public boolean movePiece(Piece piece, YutResult result, List<Player> players) {
        List<Piece> group = piece.getGroup().isEmpty() ? List.of(piece) : piece.getGroup();
        boolean captured = false;


        for (Piece p : group) {
            if (p.getCustomPath() == null) {
                PieceUtil.initializePath(p, pathStrategy);
            }
            Position newPos;
            if (result.getStep() < 0) {
                newPos = pathStrategy.getPreviousPosition(p.getPosition(), Math.abs(result.getStep()));
                p.setPosition(newPos);
                p.advancePathIndex(result.getStep());
            } else {
                newPos = pathStrategy.getNextPosition(p, result);
                p.setPosition(newPos);
                p.advancePathIndex(result.getStep());
            }
            p.setPosition(newPos);
            p.setMoved();// 이동한 말로 표시


            // 잡기: 같은 칸에 있는 상대 말들을 출발점으로
            for (Player otherPlayer : players) {
                if (otherPlayer != piece.getOwner()) {
                    for (Piece other : otherPlayer.getPieces()) {
                        if (!other.isFinished() &&
                                other.getPosition().getIndex() == newPos.getIndex()) {

                            // 잡은 말이 속한 그룹 전체 해제 + 출발점 이동
                            List<Piece> capturedGroup = other.getGroup().isEmpty() ? List.of(other) : other.getGroup();
                            for (Piece capturedp : capturedGroup) {
                                capturedp.setPosition(pathStrategy.getPath().get(0)); // 출발점
                                PieceUtil.resetGroupToSelf(capturedp);
                            }

                            captured = true;
                        }
                    }
                }
            }

            if (newPos.getIndex() == 28) {
                p.setPassedCenter(true);
            }

        }
        // 이동 완료 후 같은 위치의 같은 플레이어 말들끼리 자동 그룹 형성
        for (Piece p : group) {

            List<Piece> autoGroup = new ArrayList<>();
            for (Piece candidate : p.getOwner().getPieces()) {
                if (!candidate.isFinished() &&
                        candidate.getPosition().equals(p.getPosition())) {
                    autoGroup.add(candidate);
                }
            }
            // 그룹 무결성 보장
            PieceUtil.ensureGroupConsistency(autoGroup);
        }
        return captured;
    }
}