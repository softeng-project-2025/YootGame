package model.dto;

public enum MessageType {
    INFO, // 정상 게임 흐름 안내
    WARN, // 규칙 위반 등 사용자 경고
    ERROR // 예외, 실패 등
}