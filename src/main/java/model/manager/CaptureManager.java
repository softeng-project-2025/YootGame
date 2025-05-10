package model.manager;

import model.piece.Piece;
import model.player.Player;
import model.position.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// CaptureManager: 이동 후 말 잡기(캡처) 로직을 처리합니다.
public class CaptureManager {

    // 이동된 말의 위치에 따라 상대의 말을 찾고 잡힌 말을 리셋하여 반환합니다.
    public static List<Piece> handleCaptures(
            Map<Piece, Position> movedPositions,
            List<Player> allPlayers
    ) {
        return movedPositions.entrySet().stream()
                .flatMap(entry -> {
                    Position dest = entry.getValue();
                    Player mover = entry.getKey().getOwner();
                    return allPlayers.stream()
                            .filter(p -> !p.equals(mover))
                            .flatMap(p -> p.getPieces().stream())
                            .filter(other -> !other.isFinished() && other.getPosition().equals(dest))
                            .peek(Piece::resetToStart);
                })
                .collect(Collectors.toList());
    }

}
