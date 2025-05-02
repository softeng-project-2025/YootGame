package model.board;

import model.piece.Piece;
import model.player.Player;
import model.position.Position;
import model.yut.YutResult;
import model.strategy.PathStrategy;

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
        Position newPos = pathStrategy.getNextPosition(piece.getPosition(), result);
        piece.setPosition(newPos);

        // 잡기 처리
        boolean captured = false;
        for (Player otherPlayer : players) {
            if (otherPlayer != piece.getOwner()) {
                for (Piece otherPiece : otherPlayer.getPieces()) {
                    if (!otherPiece.isFinished() && otherPiece.getPosition().getIndex() == newPos.getIndex()) {
                        // 상대 말 잡기 → 출발점으로
                        otherPiece.setPosition(pathStrategy.getPath().get(0));
                        captured = true;
                    }
                }
            }
        }

        return captured;
    }
}