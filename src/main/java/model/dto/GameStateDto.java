package model.dto;

import model.Game;
import model.piece.Piece;
import model.player.Player;
import model.yut.YutResult;
import java.util.List;
import java.util.Map;
import java.util.EnumMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 통합 DTO: 게임 상태와 렌더링에 필요한 모든 정보를 제공합니다.
 */
public record GameStateDto(
        YutResult lastYut,
        List<PieceInfo> pieces,
        List<PlayerInfo> players,
        String messageText,
        MessageType messageType,
        boolean gameOver,
        List<YutResult> pendingYuts
) {

    /** 아무 것도 없는 초기 화면용 DTO */
    public static GameStateDto empty() {
        return new GameStateDto(
                null,            // 마지막 윷 결과 없음
                List.of(),       // 보드에는 말이 없음
                List.of(),       // pending 윷 결과 없음
                "게임을 설정해주세요", // 초기 안내 문구
                MessageType.INFO, // INFO 스타일
                false,
                List.of()
                );
    }

    /**
     * 화면에 표시할 말 정보
     */
    public static record PieceInfo(
            int id, int x, int y, boolean selectable
    ) {}

    /**
     * 화면에 표시할 플레이어 정보
     */
    public static record PlayerInfo(
            int id, String name, boolean isCurrent
    ) {}

    /**
     * 도메인 모델과 MoveResult로부터 DTO를 생성합니다.
     */
    public static GameStateDto from(
            Game game,
            MoveResult result,
            List<Piece> selectablePieces,
            String messageText,
            MessageType messageType
    ) {
        YutResult last = result != null ? result.yutResult() : null;

        List<PieceInfo> pieceInfos = game.getPlayers().stream()
                .flatMap(p -> p.getPieces().stream())
                .map(p -> {
                    var pos = p.getPosition();
                    boolean sel = selectablePieces != null && selectablePieces.contains(p);
                    return new PieceInfo(p.getId(), pos.x(), pos.y(), sel);
                })
                .collect(Collectors.toList());

        Player current = game.getTurnManager().currentPlayer();
        List<PlayerInfo> playerInfos = game.getPlayers().stream()
                .map(p -> new PlayerInfo(p.getId(), p.getName(), p.equals(current)))
                .collect(Collectors.toList());

        boolean over = game.isFinished();
        List<YutResult> pending = game.getTurnResult().getPending();

        return new GameStateDto(
                last,
                pieceInfos,
                playerInfos,
                messageText != null ? messageText : "",
                messageType != null ? messageType : MessageType.INFO,
                over,
                List.copyOf(pending)
        );
    }
}
