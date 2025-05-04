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
            players.add(new Player("Player " + i, pieceCount, board));
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
        game.handlePieceSelect(piece);
        view.renderGame(game);

        if (game.isFinished()) {
            String winner = game.getCurrentPlayer().getName();
            view.showMessage(winner + " wins!");
            view.promptRestart(this); // View가 사용자에게 묻도록 함
        }

        view.renderGame(game);
    }

    public Game getGame() {
        return game;
    }

}
