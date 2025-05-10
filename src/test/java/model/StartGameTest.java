//package model;
//import model.board.Board;
//import model.piece.Piece;
//import model.state.SelectingPieceState;
//import model.player.Player;
//import model.strategy.SquarePathStrategy;
//import model.yut.YutResult;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
////게임을 진행하는 시나리오에 관한 테스트 케이스
//public class StartGameTest {
//    private Game game;
//    private Player p1, p2;
//
//    @BeforeEach
//    void setUp() {
//        Board board = new Board(new SquarePathStrategy());
//        p1 = new Player("Player1", 4, board, 1);
//        p2 = new Player("Player2", 4, board, 2);
//        game = new Game(board, Arrays.asList(p1, p2));
//    }
//
//    /* 테스트 케이스 1: 게임을 시작하는 경우.
//     * 유저 수를 받고 모든 유저의 말과 보드, 게임 상태가 잘 초기화 되는지
//     */
//    @Test
//    void testGameInitialization() {
//        assertNotNull(game.getBoard());
//        assertEquals(2, game.getPlayers().size());
//
//        game.getPlayers().forEach(player -> {
//            assertEquals(4, player.getPieces().size());
//            player.getPieces().forEach(piece -> {
//                assertFalse(piece.isFinished());
//                assertNotNull(piece.getPosition());
//            });
//        });
//    }
//
//    /* 테스트 케이스 2: model.EndGame 상태에서 재시작을 눌르는 경우.
//     * 새로운 게임으로 잘 초기화 되는지
//     */
//}
