package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import model.board.Board;
import model.dto.MoveFailType;
import model.dto.NextStateHint;
import model.player.Player;
import model.dto.MoveResult;
import model.state.CanSelectPiece;
import model.state.CanThrowYut;
import model.state.WaitingForThrowState;
import model.state.GameState;
import model.yut.YutResult;
import model.piece.Piece;

public class Game {

    private List<Player> players;
    private Board board;
    private int currentPlayerIndex;

    private GameState currentState;
    private boolean isFinished = false;

    private final Queue<YutResult> yutQueue = new LinkedList<>();

    public Game(Board board, List<Player> players) {
        this.board = board;
        this.players = players;
        this.currentPlayerIndex = 0;
        this.currentState = new WaitingForThrowState(this); // 초기 상태
    }

    // 턴 진행: 현재 상태에 따라 동작
    public MoveResult handleYutThrow(YutResult result) {
        if (isFinished) return MoveResult.gameOver(getCurrentPlayer());
        if (!(currentState instanceof CanThrowYut throwState)) {
            return MoveResult.fail(MoveFailType.THROW_REQUIRED);  // 혹은 다른 에러 핸들링
        }

        MoveResult resultAfterThrow = throwState.handleYutThrowWithResult(result);
        applyStateTransition(resultAfterThrow);
        return resultAfterThrow;
    }

    public MoveResult handlePieceSelect(Piece piece) {
        if (isFinished) return MoveResult.gameOver(getCurrentPlayer());
        if (!(currentState instanceof CanSelectPiece selectState)) {
            return MoveResult.fail(MoveFailType.INVALID_SELECTION);  // 혹은 다른 에러 핸들링
        }

        MoveResult resultAfterMove = selectState.handlePieceSelectWithResult(piece);
        applyStateTransition(resultAfterMove);
        return resultAfterMove;
    }

    public void applyStateTransition(MoveResult result) {
        if (result == null || result.isFailure()) return;

        NextStateHint hint = result.nextStateHint();
        if (hint == null) return;

        switch (hint) {
            case WAITING_FOR_THROW -> setState(new WaitingForThrowState(this));
            case NEXT_TURN -> nextTurn(); // nextTurn 내부에서 상태 초기화
            case GAME_ENDED -> setFinished(true);
            case STAY -> {} // 아무 것도 하지 않음
        }
    }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentState = new WaitingForThrowState(this); // 다음 턴은 다시 초기 상태
    }

    // 플레이어 관련
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }



    // 종료 상태 조건
    public boolean isFinished() {
        return isFinished;
    }


    // 승자가 있으면 true, 없으면 false
    public boolean checkAndHandleWinner() {
        for (Player player : players) {
            if (player.hasFinishedAllPieces()) {
                isFinished = true;
                return true;
            }
        }
        return false;
    }

    public void setFinished(boolean finished) {
        this.isFinished = finished;
    }


    public void setState(GameState state) {
        this.currentState = state;
    }

    public GameState getState() {
        return this.currentState;
    }

    public Board getBoard() {
        return this.board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Queue<YutResult> getYutQueue() {
        return yutQueue;
    }

    public void enqueueYutResult(YutResult result) {
        yutQueue.offer(result);
    }

    public YutResult dequeueYutResult() {
        return yutQueue.poll();
    }

    public boolean hasPendingYutResults() {
        return !yutQueue.isEmpty();
    }

    public void clearYutQueue() {
        yutQueue.clear();
    }


}