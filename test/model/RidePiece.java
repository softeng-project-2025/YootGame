package model;

import model.board.Board;
import model.piece.Piece;
import model.player.Player;
import model.position.Position;
import model.state.WaitingForThrowState;
import model.strategy.SquarePathStrategy;
import model.yut.YutResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//말이 업히는 시나리오에 관한 테스트 케이스
public class RidePiece {
    private Game game;
    private Player p1, p2;
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(new SquarePathStrategy());
        p1 = new Player("Player1", 4, board);
        p2 = new Player("Player2", 4, board);
        game = new Game(board, Arrays.asList(p1, p2));
    }

    /* 테스트 케이스 1: 말이 업히는 경우.
     * 업혀진 말들의 state 가 정확한지
     */
    @Test
    void testPieceGrouping() {
        Piece p1a = p1.getPieces().get(0);
        Piece p1b = p1.getPieces().get(1);

        //시나리오 진행
        game.handleYutThrow(YutResult.GAE);
        game.handlePieceSelect(p1a);
        game.handleYutThrow(YutResult.GEOL);
        game.handlePieceSelect(p2.getPieces().get(0));
        game.handleYutThrow(YutResult.GAE);
        game.handlePieceSelect(p1b);

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

        //시나리오 진행
        game.handleYutThrow(YutResult.GAE);
        game.handlePieceSelect(p1a);
        game.handleYutThrow(YutResult.GEOL);
        game.handlePieceSelect(p2.getPieces().get(0));
        game.handleYutThrow(YutResult.GAE);
        game.handlePieceSelect(p1b);
        game.handleYutThrow(YutResult.GEOL);
        game.handlePieceSelect(p2.getPieces().get(0));

        // 윷 던지고 첫 번째 말 선택 (업히기 처리)
        game.handleYutThrow(YutResult.GEOL);
        game.handlePieceSelect(p1a);

        // 다음 위치로 이동되었는지 확인
        Position expectedPos = board.getPathStrategy().getNextPosition(board.getPathStrategy().getPath().get(2), YutResult.GEOL);

        assertEquals(expectedPos.getIndex(), p1a.getPosition().getIndex(), "첫 번째 말 이동 위치 확인");
        assertEquals(expectedPos.getIndex(), p1a.getPosition().getIndex(), "업힌 두 번째 말도 같이 이동해야 함");
    }
}
