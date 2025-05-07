package controller;

import model.Game;
import model.board.Board;
import model.dto.GameMessage;
import model.dto.GameMessageFactory;
import model.dto.MoveResult;
import model.dto.NextStateHint;
import model.player.Player;
import model.piece.Piece;
import model.state.WaitingForThrowState;
import model.strategy.PathStrategy;
import model.strategy.SquarePathStrategy;
import model.yut.YutResult;
import view.View;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private Game game;
    private final View view;

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
        game.applyStateTransition(resultAfterThrow); // 상태 전이 Game에게 위임
        view.updateYutResult(result); // 현재 누적 윷 결과 View에 갱신
        updateViewAfterMove(resultAfterThrow); // 메시지 포함 전체 처리

    }

    // 말 선택 처리
    public void handlePieceSelect(Piece piece) {
        MoveResult result = game.handlePieceSelect(piece); // 상태(State)에 위임
        game.applyStateTransition(result); // 상태 전이 Game에게 위임
        updateViewAfterMove(result); // 실패/성공 메시지 자동 처리
    }



    // 공통 View 업데이트
    private void updateViewAfterMove(MoveResult result) {
        view.renderGame(game);

        // 메시지 처리
        GameMessage msg = result.isFailure()
                ? GameMessageFactory.fromFailResult(result)
                : GameMessageFactory.fromMoveResult(result);

        view.updateStatus(msg.content(), msg.type());

        // 상태 전이 처리
        switch (result.nextStateHint()) {
            case GAME_ENDED -> {
                game.setFinished(true); // 명시적으로 종료 처리
                if (result.winner() != null) {
                    view.showWinner(result.winner());
                }
                view.promptRestart(this);
            }
            case WAITING_FOR_THROW -> {
                game.setState(new WaitingForThrowState(game));
            }
            case NEXT_TURN -> {
                game.nextTurn(); // 내부에서 WaitingForThrowState 자동 설정
            }
            case STAY -> {
                // 아무 상태 전이 없음
            }
        }
    }

}
