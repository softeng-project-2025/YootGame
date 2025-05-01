package model;

import java.util.ArrayList;
import java.util.List;

import model.board.Board;
import model.player.Player;
import model.state.GameState;
import model.state.WaitingForThrowState;
import model.yut.YutResult;
import model.piece.Piece;

public class Game {

    private List<Player> players;
    private Board board;
    private int currentPlayerIndex;

    private GameState currentState;

    public Game(Board board, List<Player> players) {
        this.board = board;
        this.players = players;
        this.currentPlayerIndex = 0;
        this.currentState = new WaitingForThrowState(this); // 초기 상태
    }

    // 턴 진행: 현재 상태에 따라 동작
    public void handleYutThrow(YutResult result) {
        currentState.handleYutThrow(result);
    }

    public void handlePieceSelect(Piece piece) {
        currentState.handlePieceSelect(piece);
    }

    // 플레이어 관련
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentState = new WaitingForThrowState(this); // 다음 턴은 다시 초기 상태
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
}