package model.board;

import model.piece.Piece;
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
    public Position getNextPosition(Position current, YutResult result) {
        return pathStrategy.getNextPosition(current, result);
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
            Position newPos = pathStrategy.getNextPosition(p.getPosition(), result);
            p.setPosition(newPos);

            // 잡기: 같은 칸에 있는 상대 말들을 출발점으로
            for (Player otherPlayer : players) {
                if (otherPlayer != piece.getOwner()) {
                    for (Piece other : otherPlayer.getPieces()) {
                        if (!other.isFinished() &&
                                other.getPosition().getIndex() == newPos.getIndex()) {
                            other.setPosition(pathStrategy.getPath().get(0)); // 출발점
                            other.setGroup(new ArrayList<>(List.of(other))); // 단독 그룹으로 설정
                            captured = true;
                        }
                    }
                }
            }
        }

        return captured;
    }
}