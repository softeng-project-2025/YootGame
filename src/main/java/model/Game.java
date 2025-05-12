package model;

import java.util.*;

import model.board.Board;
import model.manager.TurnManager;
import model.player.Player;
import model.state.GameOverState;
import model.state.WaitingForThrowState;
import model.state.GameState;
import model.turn.TurnResult;

// 게임 로직의 핵심 엔티티입니다.
// 매 턴 시작 시 TurnResult를 자동 생성하도록 변경되었습니다.
public class Game {

    private final Board board;
    private GameState currentState;
    private final List<Player> players;
    private final TurnManager turnManager;
    private TurnResult turnResult;

    public Game(Board board, List<Player> players) {
        this.board = board;
        this.players = new ArrayList<>(players);
        this.turnManager = new TurnManager(this.players);
        this.currentState = new WaitingForThrowState(this); // 초기 상태
        startTurn();
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

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public TurnManager getTurnManager() {
        return turnManager;
    }

    // 매 턴이 시작될 때 새로운 TurnResult를 생성합니다.
    public void startTurn() {
        this.turnResult = new TurnResult();
    }

    // 현재 턴의 TurnResult를 반환합니다.
    // 아직 생성되지 않았다면 startTurn()을 통해 초기화합니다.
    public TurnResult getTurnResult() {
        if (turnResult == null) {
            startTurn();
        }
        return turnResult;
    }

    // 게임이 종료 상태인지 확인합니다.
    public boolean isFinished() {
        // 1) 이미 GameOverState 인 경우
        if (currentState instanceof GameOverState) {
            return true;
        }
        // 2) 조각이 하나 이상 있고, 모든 말을 골인시킨 플레이어가 있다면
        boolean someoneWon = players.stream()
                .filter(p -> !p.getPieces().isEmpty())       // << 이 줄을 추가!
                .anyMatch(Player::hasAllPiecesFinished);
        if (someoneWon) {
            transitionTo(new GameOverState());
            return true;
        }
        // 3) 그 외에는 진행 중
        return false;
    }


    // 상태 전이를 캡슐화합니다.
    public void transitionTo(GameState next) {
        currentState.onExit(this);
        this.currentState = next;
        currentState.onEnter(this);
    }

}