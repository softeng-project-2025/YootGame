package model.manager;

import model.piece.Piece;
import model.player.Player;
import model.position.Position;

import java.util.*;
import java.util.stream.Collectors;

// CaptureManager: 한 턴 동안 이동된 말(movedPieces)을 기준으로
// 상대 말을 찾아 캡처(리셋)하고, 각 이동자별 캡처된 말을 매핑 형태로 반환합니다.
public class CaptureManager {

    // 캡처 매핑: mover -> 캡처된 말 리스트
    public Map<Piece, List<Piece>> handleCaptures(
            List<Piece> movedPieces,
            List<Player> allPlayers
    ) {
        Objects.requireNonNull(movedPieces, "movedPieces must not be null");
        Objects.requireNonNull(allPlayers, "allPlayers must not be null");

        Map<Piece, List<Piece>> captureMap = new HashMap<>();
        for (Piece mover : movedPieces) {
            int destIndex = mover.getPosition().index();
            List<Piece> captured = allPlayers.stream()
                    .filter(p -> !p.equals(mover.getOwner()))
                    .flatMap(player -> player.getPieces().stream())
                    .filter(other -> !other.isFinished() && other.getPosition().index() == destIndex)
                    .peek(Piece::resetToStart)
                    .collect(Collectors.toList());
            if (!captured.isEmpty()) {
                captureMap.put(mover, captured);
            }
        }
        return captureMap;
    }

}
