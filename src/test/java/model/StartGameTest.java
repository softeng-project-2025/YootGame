package model;
import model.board.Board;
import model.piece.Piece;
import model.player.Player;
import model.strategy.SquarePathStrategy;
import model.yut.YutResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//게임을 진행하는 시나리오에 관한 테스트 케이스
public class StartGameTest {
    private Game game;
    private Player p1, p2;
    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board(new SquarePathStrategy());
        p1 = new Player("Player1", 4, board);
        p2 = new Player("Player2", 4, board);
        List<Player> players = Arrays.asList(p1, p2);
        game = new Game(board, players);
    }

    /* 테스트 케이스 1: 게임을 시작하는 경우.
     * 유저 수를 받고 모든 유저의 말과 보드, 게임 상태가 잘 초기화 되는지
     */
    @Test
    public void testGameInitialization() {
        assertNotNull(game.getBoard(), "보드는 null이 아니어야 한다");
        assertEquals(2, game.getPlayers().size(), "플레이어 수는 2명이어야 한다");

        for (Player player : game.getPlayers()) {
            assertEquals(4, player.getPieces().size(), "각 플레이어는 4개의 말을 가져야 한다");
            for (Piece piece : player.getPieces()) {
                assertFalse(piece.isFinished(), "초기에는 모든 말이 완주하지 않은 상태여야 한다");
                assertNotNull(piece.getPosition(), "모든 말의 초기 위치는 null이 아니어야 한다");
            }
        }
    }

    /* 테스트 케이스 2: 턴이 진행되는 경우.
     * 각 유저에게 순서대로 턴이 주어지는지
     */
    @Test
    public void testTurnProgressionThroughGameFlow() {
        // 초기 플레이어는 p1
        assertEquals(p1, game.getCurrentPlayer(), "초기 턴은 Player1이어야 한다");

        // 윷을 던지고 (예: 개)
        game.handleYutThrow(YutResult.GAE);

        // 현재 플레이어의 움직일 수 있는 말을 하나 선택
        Piece selectedPiece = p1.getPieces().get(0);
        game.handlePieceSelect(selectedPiece);

        // 턴이 넘어가야 함 → 다음 플레이어는 p2
        assertEquals(p2, game.getCurrentPlayer(), "말을 이동한 후 턴은 Player2로 넘어가야 한다");

        /* *턴 종료 로직이 전부 구현 완료될 시에 추가할만한 코드
        // p2가 윷/모를 던지면, 상태는 턴을 넘기지 않음
        game.handleYutThrow(YutResult.MO);
        Piece selectedPiece2 = p2.getPieces().get(0);
        game.handlePieceSelect(selectedPiece2);

        // 턴은 여전히 p2 → 추가 턴
        assertEquals(p2, game.getCurrentPlayer(), "윷/모의 경우 추가 턴으로 인해 여전히 Player2의 턴이어야 한다");
        */
    }


    /* 테스트 케이스 3: model.EndGame 상태에서 재시작을 눌르는 경우.
     * 새로운 게임으로 잘 초기화 되는지
     */
}
