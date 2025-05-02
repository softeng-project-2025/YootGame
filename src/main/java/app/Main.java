import controller.GameController;
import view.swing.SwingView;

public class Main {
    public static void main(String[] args) {
        // SwingView 먼저 생성
        SwingView view = new SwingView();

        // Controller 생성 후 View에 주입
        GameController controller = new GameController(view);
        view.setController(controller);

        // 초기 게임 설정
        int playerCount = 2; // 예: 2인용
        int pieceCount = 3;  // 예: 말 3개
        String boardType = "square"; // "square", "pentagon", "hexagon"

        controller.initializeGame(playerCount, pieceCount, boardType);
    }
}