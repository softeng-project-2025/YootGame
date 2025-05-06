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

import static org.junit.jupiter.api.Assertions.*;

//말을 잡는 시나리오에 관한 테스트 케이스
public class CatchPieceTest {
    private Game game;
    private Player p1, p2;
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(new SquarePathStrategy());
        p1 = new Player("Player1", 4, board, 1);
        p2 = new Player("Player2", 4, board, 2);
        game = new Game(board, Arrays.asList(p1, p2));
    }

    /* 테스트 케이스 1: 다른 사용자의 말을 잡는 경우
     * 잡힌 말은 출발지로 잘 가는지
     * 잡은 player YutThrowing 기회가 한 번 더 주어지는 지
     */
    @Test
    void testCatchOpponentPieceGivesExtraTurn() {
        Piece attacker = p1.getPieces().get(0);
        Piece victim = p2.getPieces().get(0);

        // 두 말 모두 같은 위치로 배치
        Position middle = board.getPathStrategy().getPath().get(3);
        victim.setPosition(middle); // 상대방 말 먼저 해당 위치에 존재
        attacker.setPosition(board.getPathStrategy().getPath().get(2));

        // 잡기
        game.handleYutThrow(YutResult.DO);
        game.handlePieceSelect(attacker);

        // 잡힌 말 출발점으로 돌아갔는지
        Position start = board.getPathStrategy().getPath().get(0);
        assertEquals(start.getIndex(), victim.getPosition().getIndex(), "잡힌 말은 출발점으로 돌아가야 함");

        // 잡은 사람은 추가 턴을 받아야 함
        assertInstanceOf(WaitingForThrowState.class, game.getState(), "말을 잡았으므로 추가 턴이 주어져야 함");
    }


    /* 테스트 케이스 2: 다른 사용자의 업힌 말을 잡는 경우
     * 모든 잡힌 말이 출발지로 잘 가는지
     * 업힌 말들의 그룹화가 풀리는지
     */
    @Test
    void testCatchRiddenPiecesResetToStartAndUngroup() {
        Piece p2a = p2.getPieces().get(0);
        Piece p1a = p1.getPieces().get(0);
        Piece p1b = p1.getPieces().get(1);

        //시나리오
        game.handleYutThrow(YutResult.GAE);
        game.handlePieceSelect(p1a);
        game.handleYutThrow(YutResult.DO);
        game.handlePieceSelect(p2a);
        game.handleYutThrow(YutResult.GAE);
        game.handlePieceSelect(p1b);
        game.handleYutThrow(YutResult.DO);
        game.handlePieceSelect(p2a);

        // 출발점 위치
        Position start = board.getPathStrategy().getPath().get(0);

        // victim1과 victim2가 출발점으로 이동했는지
        assertEquals(start.getIndex(), p1a.getPosition().getIndex(), "잡힌 첫 번째 말은 출발점으로 돌아가야 함");
        assertEquals(start.getIndex(), p1b.getPosition().getIndex(), "잡힌 두 번째 말도 출발점으로 돌아가야 함");

        // 그룹이 해제됐는지 (각자 단독 그룹인지)
        assertEquals(1, p1a.getGroup().size(), "잡힌 말의 그룹이 해제되어야 함");
        assertEquals(p1a, p1a.getGroup().get(0), "자기 자신만 그룹에 있어야 함");

        assertEquals(1, p1b.getGroup().size(), "잡힌 말의 그룹이 해제되어야 함");
        assertEquals(p1b, p1b.getGroup().get(0), "자기 자신만 그룹에 있어야 함");
    }
}
