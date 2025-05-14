package model;

import java.util.*;

import exception.GameInitializationException;
import model.board.Board;
import model.manager.TurnManager;
import model.piece.Piece;
import model.player.Player;
import model.position.Position;
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

    // 허용 범위 상수
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 4;
    private static final int MIN_PIECES = 2;
    private static final int MAX_PIECES = 5;

    public Game(Board board, List<Player> players) {
        validatePlayers(players);
        this.board = board;
        this.players = new ArrayList<>(players);
        this.turnManager = new TurnManager(this.players);

        if (board.getStrategy() != null) {
            List<Position> basePath = board.getStrategy().getPath();
            for (Player p : this.players) {
                for (Piece piece : p.getPieces()) {
                    piece.setCustomPath(basePath);
                }
            }
        }
        this.currentState = new WaitingForThrowState(this);
        startTurn();
    }


    private void validatePlayers(List<Player> players) {
        if (players == null || players.size() < MIN_PLAYERS || players.size() > MAX_PLAYERS) {
            throw new GameInitializationException(
                    "플레이어 수는 " + MIN_PLAYERS + "명 이상, " + MAX_PLAYERS + "명 이하만 가능합니다."
            );
        }
        for (Player p : players) {
            int count = p.getPieces().size();
            if (count < MIN_PIECES || count > MAX_PIECES) {
                throw new GameInitializationException(
                        "각 플레이어는 말 개수를 " + MIN_PIECES + "개 이상, " + MAX_PIECES + "개 이하로 설정해야 합니다."
                );
            }
        }
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

    public boolean noneMovedYet(Player current){
        return current.getPieces().stream().noneMatch(Piece::hasMoved);
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

    public void reset() {
        for (Player p : players) {
            for (Piece piece : p.getPieces()) {
                piece.resetToStart(board);
            }
        }
        turnManager.reset();
        this.currentState = new WaitingForThrowState(this);
        startTurn();
    }

}