package controller;

import model.Game;
import model.board.Board;
import model.dto.GameMessage;
import model.dto.GameMessageFactory;
import model.dto.MoveResult;
import model.manager.GameService;
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
    private GameService gameService;
    private final View view;

    public GameController(View view) {
        this.view = view;
    }

    // 게임 시작 설정: 플레이어 수, 말 수, 보드 타입
    public void initializeGame(int playerCount, int pieceCount, String boardType) {
        PathStrategy pathStrategy = resolvePathStrategy(boardType);
        Board board = new Board(pathStrategy);
        List<Player> players = createPlayers(playerCount, pieceCount, board);

        this.game = new Game(board, players);
        this.gameService = new GameService(game);

        view.setController(this); // View에 컨트롤러 주입
        view.renderGame(game);    // 초기 화면 렌더링
    }

    public void startTurn() {
        gameService.startTurn();
        view.updateYutResult(null); // 던지기 결과 초기화
        view.renderGame(game);
    }

    public void throwYut() {
        YutResult result = gameService.throwAndAccumulate();
        view.updateYutResult(result);
        showSelectablePieces();
        view.renderGame(game);
    }


    public void applyResultToPiece(YutResult result, Piece piece) {
        MoveResult moveResult = gameService.applyResultToPiece(result, piece);
        updateViewAfterMove(moveResult);
    }

    // 공통 View 업데이트
    private void updateViewAfterMove(MoveResult result) {
        view.renderGame(game);
        GameMessage msg = result.isFailure()
                ? GameMessageFactory.fromFailResult(result)
                : GameMessageFactory.fromMoveResult(result);
        view.updateStatus(msg.content(), msg.type());

        if (result.isGameOver()) {
            if (result.winner() != null) {
                view.showWinner(result.winner());
            }
            view.promptRestart(this);
        }
    }

    private PathStrategy resolvePathStrategy(String boardType) {
        return switch (boardType.toLowerCase()) {
            case "pentagon", "hexagon" -> new SquarePathStrategy(); // TODO: 교체 예정
            default -> new SquarePathStrategy();
        };
    }

    private List<Player> createPlayers(int count, int pieces, Board board) {
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            players.add(new Player("Player " + i, pieces, board, i));
        }
        return players;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void showSelectablePieces() {
        List<Piece> pieces = gameService.getSelectablePieces();
        view.showSelectablePieces(pieces);
    }

}
