package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import model.board.Board;
import model.player.Player;
import model.state.GameState;
import model.dto.MoveResult;
import model.state.WaitingForThrowState;
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
        if (isFinished) {
            return MoveResult.gameOver(getCurrentPlayer());
        }
        return currentState.handleYutThrowWithResult(result);
    }
    public MoveResult handlePieceSelect(Piece piece) {
        if (isFinished) {
            return MoveResult.gameOver(getCurrentPlayer());
        }
        return currentState.handlePieceSelectWithResult(piece);
    }

    // 플레이어 관련
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentState = new WaitingForThrowState(this); // 다음 턴은 다시 초기 상태
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