package model.dto;

import model.player.Player;

public class GameMessageFactory {
    private static final String NO_MOVABLE_MESSAGE_SUFFIX = "님은 이동할 수 있는 말이 없습니다.";
    private static final String SELECT_PIECE_PROMPT_SUFFIX = "님, 이동할 말을 선택해주세요.";

    private static final String ALREADY_THROWN_MESSAGE = "이미 윷을 던졌습니다. 말을 선택하세요.";
    private static final String NO_RESULT_MESSAGE = "적용할 윷 결과가 없습니다.";

    private static final String INVALID_SELECTION_MESSAGE = "잘못된 말 선택입니다.";
    private static final String GOAL_MESSAGE_SUFFIX = "의 말이 골인했습니다!";

    private static final String CAPTURE_MESSAGE_SUFFIX = "이(가) 상대 말을 잡았습니다!";
    private static final String BONUS_TURN_SUFFIX = " 한 번 더 던지세요.";
    private static final String MOVE_MESSAGE_SUFFIX = "이(가) 말을 이동했습니다.";
    private static final String YUT_OR_MO_MESSAGE_SUFFIX = "이(가) 윷 또는 모로 추가 턴을 얻었습니다.";

    private static final String NEXT_RESULT_SUFFIX = " 다음 결과를 적용할 말을 선택하세요.";
    private static final String NEXT_TURN_SUFFIX = " 다음 플레이어의 차례입니다.";
    private static final String THROW_PROMPT_SUFFIX = " 추가 턴입니다. 윷을 던지세요.";


    public static GameMessage noMovablePieceMessage(Player player) {
        return new GameMessage(player.getName() + NO_MOVABLE_MESSAGE_SUFFIX, MessageType.INFO);
    }

    public static GameMessage selectPiecePrompt(Player player) {
        return new GameMessage(player.getName() + SELECT_PIECE_PROMPT_SUFFIX, MessageType.INFO);
    }

    public static GameMessage invalidSelectionMessage() {
        return new GameMessage(INVALID_SELECTION_MESSAGE, MessageType.WARN);
    }

    public static GameMessage alreadyThrownMessage() {
        return new GameMessage(ALREADY_THROWN_MESSAGE, MessageType.WARN);
    }

    public static GameMessage noResultMessage() {
        return new GameMessage(NO_RESULT_MESSAGE, MessageType.WARN);
    }


    public static GameMessage goalMessage(String playerName) {
        return new GameMessage(playerName + GOAL_MESSAGE_SUFFIX, MessageType.INFO);
    }

    public static GameMessage captureMessage(String playerName, boolean bonusTurn) {
        String msg = playerName + CAPTURE_MESSAGE_SUFFIX;
        return new GameMessage(bonusTurn ? msg + BONUS_TURN_SUFFIX : msg, MessageType.INFO);
    }

    public static GameMessage moveMessage(String playerName) {
        return new GameMessage(playerName + MOVE_MESSAGE_SUFFIX, MessageType.INFO);
    }

    public static GameMessage yutOrMoMessage(String playerName) {
        return new GameMessage(playerName + YUT_OR_MO_MESSAGE_SUFFIX, MessageType.INFO);
    }


    public static GameMessage withNextResultPrompt(GameMessage base) {
        return new GameMessage(base.getContent() + NEXT_RESULT_SUFFIX, base.getType());
    }

    public static GameMessage withNextTurnPrompt(GameMessage base) {
        return new GameMessage(base.getContent() + NEXT_TURN_SUFFIX, base.getType());
    }

    public static GameMessage withThrowPrompt(GameMessage base) {
        return new GameMessage(base.getContent() + THROW_PROMPT_SUFFIX, base.getType());
    }

    public static GameMessage gameAlreadyFinishedMessage() {
        return new GameMessage("게임이 이미 종료되었습니다.", MessageType.INFO);
    }

    public static GameMessage yutThrownPrompt(String playerName, String resultName) {
        return new GameMessage(playerName + "이(가) 윷을 던졌습니다: " + resultName + ". 말을 선택하세요.", MessageType.INFO);
    }

    public static GameMessage throwRequiredMessage() {
        return new GameMessage("아직 윷을 던지지 않았습니다!", MessageType.WARN);
    }
}