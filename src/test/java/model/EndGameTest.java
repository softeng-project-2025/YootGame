package model;

import model.board.Board;
import model.piece.Piece;
import model.player.Player;
import model.strategy.SquarePathStrategy;
import model.position.Position;
import model.yut.YutResult;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

//게임이 끝나는 시나리오에 관한 테스트 케이스
public class EndGameTest {
    private Game game;
    private Player p1, p2;
    private Board board;

    /* 테스트 케이스1: 한 사용자의 모든 말이 도착점을 통과함.
     * -> 즉 사용자의 piece 수가 0일 될때
     * 해당 유저가 승리 후 게임이 정상적으로 종료되는지
     */
    @Test
    void testPlayerWinsWhenAllPiecesArrived() {
        board = new Board(new SquarePathStrategy());
        p1 = new Player("Player1", 2, board);
        p2 = new Player("Player2", 2, board);
        game = new Game(board, Arrays.asList(p1, p2));

        // 모든 말의 위치를 마지막 바로 전 칸으로 이동
        Position preFinish = board.getPathStrategy().getPath().get(
                board.getPathStrategy().getPath().size() - 2
        );

        for (Piece piece : p1.getPieces()) {
            piece.setPosition(preFinish);
        }

        //게임 간이 진행
        game.handleYutThrow(YutResult.GAE);
        game.handlePieceSelect(p1.getPieces().get(0));
        game.handleYutThrow(YutResult.GAE);
        game.handlePieceSelect(p2.getPieces().get(0));
        game.handleYutThrow(YutResult.GAE);
        game.handlePieceSelect(p1.getPieces().get(1));
        game.handleYutThrow(YutResult.GAE);
        game.handlePieceSelect(p2.getPieces().get(1));

        assertFalse(p2.hasFinishedAllPieces(), "Player2는 말을 도착시키지 않았기 때문에 승리하지 않아야 함");
        assertTrue(p1.hasFinishedAllPieces(), "Player1의 모든 말이 도착했기 때문에 승리 조건이 만족되어야 함");
    }

}
