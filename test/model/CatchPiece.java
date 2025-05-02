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
public class CatchPiece {
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
}
