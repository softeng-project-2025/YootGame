package model;

import java.util.ArrayList;
import java.util.List;

import model.board.Board;
import model.dto.GameMessage;
import model.dto.MessageType;
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
    private GameMessage lastMessage;

    public Game(Board board, List<Player> players) {
        this.board = board;
        this.players = players;
        this.currentPlayerIndex = 0;
        this.currentState = new WaitingForThrowState(this); // 초기 상태
    }

    // 턴 진행: 현재 상태에 따라 동작
    public MoveResult handleYutThrow(YutResult result) {
        if (isFinished) {
            String msg = "게임이 이미 종료되었습니다.";
            GameMessage message = new GameMessage(msg, MessageType.INFO);
            this.setLastMessage(message);

            return new MoveResult(false, true, getCurrentPlayer(), false, false);
        }
        return currentState.handleYutThrowWithResult(result);
    }
    public MoveResult handlePieceSelect(Piece piece) {

        if (isFinished) {
            String msg = "게임이 이미 종료되었습니다.";
            GameMessage message = new GameMessage(msg, MessageType.INFO);
            this.setLastMessage(message);
            return new MoveResult( false, true, getCurrentPlayer(), false, false);
        }

        MoveResult result = currentState.handlePieceSelectWithResult(piece);
        return result;
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

    public void setLastMessage(GameMessage message) {
        this.lastMessage = message;
    }
    public GameMessage getLastMessage() {
        return this.lastMessage;
    }


}