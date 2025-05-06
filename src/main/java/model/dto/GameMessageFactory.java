package model.dto;

import model.piece.Piece;
import model.player.Player;

public class GameMessageFactory {

    // 메시지 본문 (player name 필요)
    private static final String SUFFIX_NO_MOVABLE = "님은 이동할 수 있는 말이 없습니다.";
    private static final String SUFFIX_SELECT_PIECE = "님, 이동할 말을 선택해주세요.";
    private static final String SUFFIX_GOAL = "의 말이 골인했습니다!";
    private static final String SUFFIX_CAPTURE = "이(가) 상대 말을 잡았습니다!";
    private static final String SUFFIX_BONUS_TURN = " 한 번 더 던지세요.";
    private static final String SUFFIX_MOVE = "이(가) 말을 이동했습니다.";
    private static final String SUFFIX_YUT_OR_MO = "이(가) 윷 또는 모로 추가 턴을 얻었습니다.";
    private static final String YUT_THROWN_PREFIX = "이(가) 윷을 던졌습니다: ";
    private static final String PROMPT_SELECT = ". 말을 선택하세요.";

    // 전체 문장 (이름 필요 없음)
    private static final String MSG_INVALID_SELECTION = "잘못된 말 선택입니다.";
    private static final String MSG_ALREADY_THROWN = "이미 윷을 던졌습니다. 말을 선택하세요.";
    private static final String MSG_NO_RESULT = "적용할 윷 결과가 없습니다.";
    private static final String MSG_THROW_REQUIRED = "아직 윷을 던지지 않았습니다!";
    private static final String MSG_GAME_ENDED = "게임이 이미 종료되었습니다.";

    // 후속 안내
    private static final String SUFFIX_NEXT_RESULT = " 다음 결과를 적용할 말을 선택하세요.";
    private static final String SUFFIX_NEXT_TURN = " 다음 플레이어의 차례입니다.";
    private static final String SUFFIX_THROW_PROMPT = " 추가 턴입니다. 윷을 던지세요.";

    // 메시지 생성 메서드들

    public static GameMessage createNoMovablePieceMessage(Player player) {
        return info(player.getName() + SUFFIX_NO_MOVABLE);
    }

    public static GameMessage createSelectPieceMessage(Player player) {
        return info(player.getName() + SUFFIX_SELECT_PIECE);
    }

    public static GameMessage goalMessage(String playerName) {
        return info(playerName + SUFFIX_GOAL);
    }

    public static GameMessage captureMessage(String playerName, boolean bonusTurn) {
        String msg = playerName + SUFFIX_CAPTURE;
        return info(bonusTurn ? msg + SUFFIX_BONUS_TURN : msg);
    }

    public static GameMessage moveMessage(String playerName) {
        return info(playerName + SUFFIX_MOVE);
    }

    public static GameMessage yutOrMoMessage(String playerName) {
        return info(playerName + SUFFIX_YUT_OR_MO);
    }

    public static GameMessage yutThrownPrompt(String playerName, String resultName) {
        return info(playerName + YUT_THROWN_PREFIX + resultName + PROMPT_SELECT);
    }

    public static GameMessage withNextResultPrompt(GameMessage base) {
        return new GameMessage(base.content() + SUFFIX_NEXT_RESULT, base.type());
    }

    public static GameMessage withNextTurnPrompt(GameMessage base) {
        return new GameMessage(base.content() + SUFFIX_NEXT_TURN, base.type());
    }

    public static GameMessage withThrowPrompt(GameMessage base) {
        return new GameMessage(base.content() + SUFFIX_THROW_PROMPT, base.type());
    }



    // 경고/예외 메시지
    public static GameMessage invalidSelectionMessage() {
        return warn(MSG_INVALID_SELECTION);
    }

    public static GameMessage alreadyThrownMessage() {
        return warn(MSG_ALREADY_THROWN);
    }

    public static GameMessage noResultMessage() {
        return warn(MSG_NO_RESULT);
    }

    public static GameMessage throwRequiredMessage() {
        return warn(MSG_THROW_REQUIRED);
    }

    public static GameMessage gameAlreadyFinishedMessage() {
        return warn(MSG_GAME_ENDED);
    }

    // 내부 헬퍼
    private static GameMessage info(String msg) {
        return new GameMessage(msg, MessageType.INFO);
    }

    private static GameMessage warn(String msg) {
        return new GameMessage(msg, MessageType.WARN);
    }

    // 말 이동 메시지
    public static GameMessage moveMessage(Piece movedPiece) {
        return info(movedPiece.getOwner().getName() + SUFFIX_MOVE);
    }

    // 상대 말을 잡은 메시지 (+ 보너스 턴 여부 반영)
    public static GameMessage captureMessage(Piece capturedPiece, boolean bonusTurn) {
        String base = capturedPiece.getOwner().getName() + SUFFIX_CAPTURE;
        return info(bonusTurn ? base + SUFFIX_BONUS_TURN : base);
    }

    // 승자 메시지
    public static GameMessage winnerMessage(Player winner) {
        return new GameMessage(winner.getName() + "님이 승리하셨습니다!", MessageType.GAME_OVER);
    }

    // MoveResult 기반 단일 진입점
    public static GameMessage fromMoveResult(MoveResult result) {
        GameMessage base;

        // 1. 기본 메시지: 행동 결과
        if (result.gameEnded()) {
            if (result.winner() != null) {
                base = winnerMessage(result.winner());
            } else {
                base = gameAlreadyFinishedMessage();
            }
        } else if (result.captured()) {
            base = captureMessage(result.movedPiece().getOwner().getName(), result.bonusTurn());
        } else {
            base = moveMessage(result.movedPiece().getOwner().getName());
        }

        // 2. 후속 메시지 조립 (행동 유도)
        if (result.turnSkipped()) {
            base = withNextTurnPrompt(base); // 말 이동할 수 없었음
        } else if (result.hasPendingYutResults()) {
            base = withNextResultPrompt(base); //큐에 남은 윷 결과 처리
        } else if (result.bonusTurn()) {
            base = withThrowPrompt(base); // 윷/모 or 잡기
        } else if (!result.gameEnded()) {
            base = withNextTurnPrompt(base);
        }

        return base;
    }

    public static GameMessage fromFailResult(MoveResult result) {
        return switch (result.failType()) {
            case INVALID_SELECTION -> invalidSelectionMessage();
            case ALREADY_THROWN -> alreadyThrownMessage();
            case NO_RESULT -> noResultMessage();
            case THROW_REQUIRED -> throwRequiredMessage();
            case GAME_ENDED -> gameAlreadyFinishedMessage();
            default -> new GameMessage("알 수 없는 오류가 발생했습니다.", MessageType.WARN);
        };
    }

    public static void log(GameMessage msg) {
        System.out.println("[GameMessage][" + msg.type() + "] " + msg.content());
    }
}