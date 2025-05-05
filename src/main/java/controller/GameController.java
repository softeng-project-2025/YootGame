package controller;

import model.Game;
import model.board.Board;
import model.dto.GameMessage;
import model.dto.MoveResult;
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

    // 윷 던지기(랜덤 or 지정)
    public void handleYutThrow(YutResult result) {
        MoveResult moveResult = game.handleYutThrow(result);
        view.updateYutResult(result);
        view.renderGame(game);
        GameMessage msg = game.getLastMessage();
        view.updateStatus(msg.getText(), msg.getType());

        if (moveResult.isGameFinished()) {
            if (moveResult.getWinner() != null) {
                view.showWinner(moveResult.getWinner());
            }
            view.promptRestart(this);
            return;
        }

        if (moveResult.isTurnSkipped()) {
            game.nextTurn();
        }
    }

    // 말 선택
    public void handlePieceSelect(Piece piece) {
        // 말 선택 처리
        MoveResult result = game.handlePieceSelect(piece);

        // 말 이동 후 렌더링
        view.renderGame(game);
        GameMessage msg = game.getLastMessage();
        view.updateStatus(msg.getText(), msg.getType());

        // 혹시라도 게임이 다른 이유로 종료됐을 경우
        if (result.isGameFinished()) {
            if (result.getWinner() != null) {
                view.showWinner(result.getWinner());
            }
            view.promptRestart(this);
            return;
        }

        if (!result.isBonusTurn()) {
            game.nextTurn(); // 다음 플레이어로 넘어감
        }
    }


    public Game getGame() {
        return game;
    }



}
