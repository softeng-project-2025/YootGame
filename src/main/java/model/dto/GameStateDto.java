package model.dto;

import model.Game;
import model.piece.Piece;
import model.player.Player;
import model.yut.YutResult;

import java.util.List;
import java.util.stream.Collectors;

// 통합 DTO: 게임 상태와 렌더링에 필요한 모든 정보를 한 곳에 모읍니다.

public record GameStateDto(
        YutResult yutResult,
        List<PieceInfo> pieces,
        List<PlayerInfo> players,
        String messageText,
        MessageType messageType,
        boolean gameOver
) {

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
     * 도메인 모델과 View 렌더링에 필요한 데이터로부터 통합 DTO 생성
     *
     * @param game 게임 도메인 객체
     * @param result 마지막 MoveResult (null 가능)
     * @param selectablePieces 이번 선택 가능 말 리스트
     * @param messageText 뷰에 표시할 상태 메시지
     * @param messageType 메시지 타입
     * @return GameStateDto 인스턴스
     */
    public static GameStateDto from(
            Game game,
            MoveResult result,
            List<Piece> selectablePieces,
            String messageText,
            MessageType messageType
    ) {
        // 윷 결과
        YutResult yut = result != null ? result.yutResult() : null;

        // 도메인에서 모든 말 수집 (Player 기준)
        List<PieceInfo> pieces = game.getPlayers().stream()
                .flatMap(p -> p.getPieces().stream())
                .map(p -> {
                    var pos = p.getPosition();
                    boolean sel = selectablePieces != null && selectablePieces.contains(p);
                    return new PieceInfo(p.getId(), pos.x(), pos.y(), sel);
                })
                .collect(Collectors.toList());

        // 플레이어 정보
        Player current = game.getTurnManager().currentPlayer();
        List<PlayerInfo> players = game.getPlayers().stream()
                .map(pl -> new PlayerInfo(
                        pl.getId(), pl.getName(), pl.equals(current)
                ))
                .collect(Collectors.toList());

        return new GameStateDto(
                yut,
                pieces,
                players,
                messageText != null ? messageText : "",
                messageType != null ? messageType : MessageType.INFO,
                game.isFinished()
        );
    }
}
