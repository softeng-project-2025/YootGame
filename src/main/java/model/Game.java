package model;

import java.util.*;

import model.board.Board;
import model.manager.TurnManager;
import model.player.Player;
import model.state.WaitingForThrowState;
import model.state.GameState;
import model.turn.TurnResult;

public class Game {

    private final Board board;
    private GameState currentState;
    private boolean isFinished = false;
    private final List<Player> players;
    private final TurnManager turnManager;
    private TurnResult turnResult;

    public Game(Board board, List<Player> players) {
        this.board = board;
        this.players = players;
        this.turnManager = new TurnManager(players);
        this.currentState = new WaitingForThrowState(this); // 초기 상태
    }

    public Board getBoard() {
        return board;
    }

    public void setState(GameState state) {
        this.currentState = state;
    }

    public GameState getState() {
        return this.currentState;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        this.isFinished = finished;
    }

    public TurnManager getTurnManager() {
        return turnManager;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    // TurnResult 관련
    public void newTurnResult() {
        this.turnResult = new TurnResult();
    }

    public TurnResult getTurnResult() {
        return this.turnResult;
    }

    public void clearTurnResult() {
        if (turnResult != null) turnResult.clear();
    }

}