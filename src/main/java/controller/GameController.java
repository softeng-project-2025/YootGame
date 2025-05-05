package controller;

import model.Game;
import model.board.Board;
import model.player.Player;
import model.piece.Piece;
import model.strategy.PathStrategy;
import model.strategy.SquarePathStrategy;
import model.yut.YutResult;
import view.View;
import view.swing.SwingView; // 필요 시 JavaFXView 등으로 교체 가능

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private Game game;
    private View view; // 인터페이스

    public GameController(View view) {
        this.view = view;
    }

    // 게임 시작 설정: 플레이어 수, 말 수, 보드 타입
    public void initializeGame(int playerCount, int pieceCount, String boardType) {
        PathStrategy pathStrategy;

        switch (boardType.toLowerCase()) {
            case "pentagon":
                // pathStrategy = new PentagonPathStrategy(); // TODO: 구현 시 교체
                pathStrategy = new SquarePathStrategy(); // 임시
                break;
            case "hexagon":
                // pathStrategy = new HexPathStrategy(); // TODO: 구현 시 교체
                pathStrategy = new SquarePathStrategy(); // 임시
                break;
            default:
                pathStrategy = new SquarePathStrategy();
        }

        Board board = new Board(pathStrategy);
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= playerCount; i++) {
            players.add(new Player("Player " + i, pieceCount, board, i));
        }

        this.game = new Game(board, players);
        view.setController(this); // View에 컨트롤러 주입
        view.renderGame(game);    // 초기 화면 렌더링
        game.setView(view); // initializeGame() 이후에 호출
    }

    // 윷 던지기(랜덤 or 지정)
    public void handleYutThrow(YutResult result) {
        game.handleYutThrow(result);
        view.updateYutResult(result);
        view.renderGame(game);
    }

    // 말 선택
    public void handlePieceSelect(Piece piece) {
        // 현재 턴 플레이어 참조
        Player currentPlayer = game.getCurrentPlayer();
        view.updateStatus(currentPlayer.getName() + "의 차례입니다. 윷을 던지세요.");

        // 말 선택 처리
        boolean captured = game.handlePieceSelect(piece); // 변경 필요 (다음 단계 참고)
        // 말 이동 후 렌더링
        view.renderGame(game);
        view.updateStatus("말을 선택하고 이동했습니다.");

        if (captured) {
            view.updateStatus(currentPlayer.getName() + "이(가) 상대 말을 잡았습니다! 한 번 더 던지세요.");
        }

        // 혹시라도 게임이 다른 이유로 종료됐을 경우
        if (game.isFinished()) {
            String winner = currentPlayer.getName();
            view.showMessage(winner + " wins!");
            view.updateStatus(winner + "님이 승리했습니다!");
            view.promptRestart(this);
            return;
        }

        // 플레이어가 말 전부 도착시켰는지 확인
        if (currentPlayer.hasFinishedAllPieces()) {
            view.showMessage(currentPlayer.getName() + "님이 모든 말을 도착시켜 승리했습니다!");
            game.setFinished(true);
            view.showWinner(currentPlayer);
            return;
        }

        // 마지막 렌더링
        view.renderGame(game);
    }


    public Game getGame() {
        return game;
    }

}
