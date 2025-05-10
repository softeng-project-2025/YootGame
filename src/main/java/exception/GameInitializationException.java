package exception;

// 게임 초기화 중 필요한 설정(플레이어 리스트 등)이 잘못되었을 때 던져지는 예외
public class GameInitializationException extends RuntimeException {
    public GameInitializationException(String message) {
        super(message);
    }
    public GameInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}