package exception;


// 잘못된 이동 시 던져지는 런타임 예외
public class InvalidMoveException extends RuntimeException {
    public InvalidMoveException(String message) {
        super(message);
    }
}
