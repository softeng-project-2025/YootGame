package model;

import model.board.Board;
import model.piece.Piece;
import model.player.Player;
import model.state.SelectingPieceState;
import model.strategy.SquarePathStrategy;
import model.position.Position;
import model.yut.YutResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//게임이 끝나는 시나리오에 관한 테스트 케이스
public class EndGameTest {
    private Game   game;
    private Player p1, p2;
    private Board  board;
    private List<Position> path;   // 편의를 위해 캐싱

    @BeforeEach
    void setUp() {
        board = new Board(new SquarePathStrategy());
        path  = board.getPathStrategy().getPath();

        p1 = new Player("Player‑1", 2, board, 1);
        p2 = new Player("Player‑2", 2, board, 2);

        game = new Game(board, Arrays.asList(p1, p2));
    }

    /** 유틸 – 윷을 큐에 적재한 뒤 SelectingPieceState 로 전환해서
     *  곧바로 말을 선택할 수 있도록 해준다.
     */
    private void throwAndSelect(YutResult result, Piece piece) {
        game.handleYutThrow(result);                          // 윷 결과를 큐에 적재
        game.setState(new SelectingPieceState(game, result)); // 상태 수동 전환
        game.handlePieceSelect(piece);                        // 실제 이동 수행
    }

    /* 테스트 케이스1: 한 사용자의 모든 말이 도착점을 통과함.
     * -> 즉 사용자의 piece 수가 0일 될때
     * 해당 유저가 승리 후 게임이 정상적으로 종료되는지
     */
    @Test
    void testPlayerWinsWhenAllPiecesArrived() {

        int finishIdx      = path.size() - 1;   // 외곽 코스에서 finish = 마지막 인덱스
        int beforeFinish   = finishIdx - 1;     // DO(1) 던지면 도착
        Position preGoal   = path.get(beforeFinish);

        //게임 간이 진행
        p1.getPieces().get(0).setCustomPath(path);
        p1.getPieces().get(0).setPosition(preGoal);
        throwAndSelect(YutResult.GAE,p1.getPieces().get(0));
        throwAndSelect(YutResult.GAE,p2.getPieces().get(0));
        p1.getPieces().get(1).setCustomPath(path);
        p1.getPieces().get(1).setPosition(preGoal);
        throwAndSelect(YutResult.GAE,p1.getPieces().get(1));
        throwAndSelect(YutResult.GAE,p2.getPieces().get(0));

        //디버깅용 이었던 것
        //assertEquals(0, p1.getPieces().get(1).getPosition().getIndex(), "잘 이동 되었는가 p1a");
        //assertEquals(0, p1.getPieces().get(0).getPosition().getIndex(), "잘 이동 되었는가 p1b");
        assertFalse(p2.hasFinishedAllPieces(), "Player2는 말을 도착시키지 않았기 때문에 승리하지 않아야 함");
        assertTrue(p1.hasFinishedAllPieces(), "Player1의 모든 말이 도착했기 때문에 승리 조건이 만족되어야 함");
    }

}
