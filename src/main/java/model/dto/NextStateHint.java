package model.dto;

public enum NextStateHint {
    SELECTING_PIECE,     // 상태 유지
    NEXT_TURN,           // 다음 플레이어로 턴 넘기기
    WAITING_FOR_THROW,   // 추가 턴, 윷 던지기 기다리기
    GAME_ENDED           // 게임 종료
}
