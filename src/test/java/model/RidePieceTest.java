package model;

import model.board.Board;
import model.service.GameService;
import model.piece.Piece;
import model.player.Player;
import model.position.Position;
import model.state.SelectingPieceState;
import model.strategy.SquarePathStrategy;
import model.yut.YutResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//말이 업히는 시나리오에 관한 테스트 케이스
public class RidePieceTest {
    private Game game;
    private Player p1, p2;
    private Board board;
    private List<Position> path;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        board = new Board(new SquarePathStrategy());
        path = board.getStrategy().getPath();

        p1 = new Player("Player1", 4, board, 1);
        p2 = new Player("Player2", 4, board, 2);
        game = new Game(board, Arrays.asList(p1, p2));
    }

    /** 유틸 – 윷을 큐에 적재한 뒤 SelectingPieceState 로 전환해서 곧바로 말을 선택할 수 있도록 한다. */
    private void throwAndSelect(YutResult result, Piece piece) {
        gameService.handleYutThrow(result);
        game.setState(new SelectingPieceState(game, result));
        gameService.handlePieceSelect(piece);
    }

    /* 테스트 케이스 1: 말이 업히는 경우.
     * 업혀진 말들의 state 가 정확한지
     */
    @Test
    void testPieceGrouping() {
        Piece p1a = p1.getPieces().get(0);
        Piece p1b = p1.getPieces().get(1);

        // 말 1을 한 칸(GAE) 이동
        throwAndSelect(YutResult.GAE, p1a);  // index 1로 이동

        // 중간에 Player2 턴 (게임 로직상 필요)
        throwAndSelect(YutResult.DO, p2.getPieces().get(0));

        // 말 2를 한 칸(GAE) 이동 → 같은 위치 (index 1)
        throwAndSelect(YutResult.GAE, p1b);

        // 업힌 말들은 그룹을 공유해야 함
        List<Piece> group = p1a.getGroup();

        assertEquals(2, group.size(), "같은 위치의 말 2개는 그룹화되어야 함");
        assertTrue(group.contains(p1a), "첫 번째 말은 그룹에 포함되어야 함");
        assertTrue(group.contains(p1b), "두 번째 말도 그룹에 포함되어야 함");

        // 서로의 그룹 정보가 일치하는지 확인
        assertEquals(p1a.getGroup(), p1b.getGroup(), "업힌 말들은 동일한 그룹 객체를 공유해야 함");
    }


    /* 테스트 케이스 2: 업힌 말이 이동하는 경우.
     * 업힌 말들이 하나의 말처럼 동시에 잘 움직이는 지
     */
    @Test
    void testGroupMoveTogether() {
        Piece p1a = p1.getPieces().get(0);
        Piece p1b = p1.getPieces().get(1);

        // 말 1을 두 칸(GEOL = 3칸) 이동 → index 3
        throwAndSelect(YutResult.GEOL, p1a);  // index 3

        // 중간 턴
        throwAndSelect(YutResult.DO, p2.getPieces().get(0));

        // 말 2를 두 칸(GEOL = 3칸) 이동 → index 3 (같은 위치)
        throwAndSelect(YutResult.GEOL, p1b);

        throwAndSelect(YutResult.DO, p2.getPieces().get(0));

        throwAndSelect(YutResult.DO, p1a);

        assertEquals(p1a.getPosition(), p1b.getPosition(), "업힌 말들은 함께 이동해야 함");
    }
}
