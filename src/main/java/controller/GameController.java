package controller;

import model.Game;
import model.board.Board;
import model.dto.GameMessage;
import model.dto.GameMessageFactory;
import model.dto.MessageType;
import model.dto.MoveResult;
import model.player.Player;
import model.piece.Piece;
import model.strategy.PathStrategy;
import model.strategy.SquarePathStrategy;
import model.yut.YutResult;
import view.View;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private Game game;
    private View view;

    public GameController(View view) {
        this.view = view;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    // 게임 시작 설정: 플레이어 수, 말 수, 보드 타입
    public void initializeGame(int playerCount, int pieceCount, String boardType) {
        PathStrategy pathStrategy = switch (boardType.toLowerCase()) {
            case "square" -> new SquarePathStrategy();
            case "pentagon" ->
                // pathStrategy = new PentagonPathStrategy(); // TODO: 구현 시 교체
                    new SquarePathStrategy(); // 임시
            case "hexagon" ->
                // pathStrategy = new HexPathStrategy(); // TODO: 구현 시 교체
                    new SquarePathStrategy(); // 임시
            default -> new SquarePathStrategy();
        };

        Board board = new Board(pathStrategy);
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= playerCount; i++) {
            players.add(new Player("Player " + i, pieceCount, board, i));
        }

        this.game = new Game(board, players);
        view.setController(this); // View에 컨트롤러 주입
        view.renderGame(game);    // 초기 화면 렌더링
    }

    // 윷 던지기 처리
    public void handleYutThrow(YutResult result) {
        MoveResult resultAfterThrow = game.handleYutThrow(result); // 상태(State)에 위임
        view.updateYutResult(result); // 현재 누적 윷 결과 View에 갱신
        GameMessage prompt = GameMessageFactory.yutThrownPrompt(game.getCurrentPlayer().getName(), result.getName());
        view.updateStatus(prompt.getContent(), prompt.getType());
        updateViewAfterMove(resultAfterThrow);
    }

    // 말 선택 처리
    public void handlePieceSelect(Piece piece) {
        MoveResult result = game.handlePieceSelect(piece); // 상태(State)에 위임
        updateViewAfterMove(result);
    }

    // 공통 View 업데이트
    private void updateViewAfterMove(MoveResult result) {
        view.renderGame(game);
        GameMessage msg = game.getLastMessage();
        view.updateStatus(msg.getContent(), msg.getType());

        if (result.gameEnded()) {
            if (result.winner() != null) {
                view.showWinner(result.winner());
            }
            view.promptRestart(this);
        }
    }

}
