package model.dto;

import model.Game;
import model.piece.Piece;
import model.player.Player;
import model.yut.YutResult;

import java.util.*;
import java.util.stream.Collectors;

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
        List<YutResult> pendingYuts,
        Phase phase
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
                List.of(),
                null
                );
    }

    /**
     * 화면에 표시할 말 정보
     */
    public static record PieceInfo(
            int id,
            int ownerId,
            int x,
            int y,
            boolean selectable
    ) {}

    /**
     * 화면에 표시할 플레이어 정보
     */
    public static record PlayerInfo(
            int id,
            String name,
            boolean isCurrent
    ) {}

    public record PositionKey(int ownerId, int x, int y) {}

    /** 동일 소유자·좌표에 있는 PieceInfo 들을 묶어서 반환 */
    public Map<PositionKey, List<PieceInfo>> groupByPosition() {
        return pieces.stream()
                .collect(Collectors.groupingBy(
                        pi -> new PositionKey(pi.ownerId(), pi.x(), pi.y())
                ));
    }

    /** 마지막 윷 결과를 꺼내와서 레이블에 표시 */
    public String findLastYut() {
        return Optional.ofNullable(lastYut)
                .map(YutResult::getName)
                .orElse("");
    }

    /** 현재 플레이어 반환 */
    public String findCurrentPlayer() {
        return players().stream()
                .filter(PlayerInfo::isCurrent)
                .map(PlayerInfo::name)
                .findFirst()
                .orElse("–");
    }


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

        // 선택 가능한 말 확인을 위한 HashSet
        Set<Piece> selectableSet = selectablePieces != null
                ? new HashSet<>(selectablePieces)
                : Collections.emptySet();

        // PieceInfo 생성
        List<PieceInfo> infos = game.getPlayers().stream()
                .flatMap(p -> p.getPieces().stream())
                .map(piece -> {
                    var pos = piece.getPosition();
                    return new PieceInfo(
                            piece.getId(),
                            piece.getOwner().getId(),
                            positionToX(pos),
                            positionToY(pos),
                            selectableSet.contains(piece)
                    );
                })
                .collect(Collectors.toList());

        // PlayerInfo 생성
        Player current = game.getTurnManager().currentPlayer();
        List<PlayerInfo> playerInfos = game.getPlayers().stream()
                .map(p -> new PlayerInfo(
                        p.getId(),
                        p.getName(),
                        p.equals(current)
                ))
                .collect(Collectors.toList());

        boolean over = game.isFinished();
        List<YutResult> pending = List.copyOf(game.getTurnResult().getPending());

        Phase phase = getPhase(result, over);

        return new GameStateDto(
                last,
                infos,
                playerInfos,
                messageText != null ? messageText : "",
                messageType != null ? messageType : MessageType.INFO,
                over,
                List.copyOf(pending),
                phase
        );
    }

    private static Phase getPhase(MoveResult result, boolean over) {
        NextStateHint hint = result != null ? result.nextStateHint() : null;
        // ① NextStateHint 보고 Phase 결정
        Phase phase;
        if (over) {
            phase = Phase.GAME_OVER;
        } else if (hint == NextStateHint.WAITING_FOR_THROW) {
            // 보너스턴이거나 윷·모 후 다시 던져야 할 때
            phase = Phase.CAN_THROW;
        } else if (hint == NextStateHint.STAY) {
            // 같은 턴에 여러 말 선택만 반복할 때
            phase = Phase.CAN_SELECT;
        } else {
            // 초기 상태, NEXT_TURN 후, 또는 지정 던지기 직후
            phase = Phase.CAN_THROW;
        }

        return phase;
    }

    private static int positionToX(model.position.Position pos) {
        return pos.x();
    }

    private static int positionToY(model.position.Position pos) {
        return pos.y();
    }
}
