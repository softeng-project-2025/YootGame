package model;

import model.board.Board;
import model.service.GameService;
import model.piece.Piece;
import model.player.Player;
import model.position.Position;
import model.state.SelectingPieceState;
import model.state.WaitingForThrowState;
import model.strategy.SquarePathStrategy;
import model.yut.YutResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//말을 잡는 시나리오에 관한 테스트 케이스
public class CatchPieceTest {
    private Game   game;
    private Player p1, p2;
    private Board  board;
    private List<Position> path;   // 편의를 위해 캐싱
    private GameService gameService;

    @BeforeEach
    void setUp() {
        board = new Board(new SquarePathStrategy());
        path  = board.getStrategy().getPath();

        p1 = new Player("Player‑1", 4, board, 1);
        p2 = new Player("Player‑2", 4, board, 2);

        game = new Game(board, Arrays.asList(p1, p2));
    }

    /** 유틸 – 윷을 큐에 적재한 뒤 SelectingPieceState 로 전환해서
     *  곧바로 말을 선택할 수 있도록 해준다.
     */
    private void throwAndSelect(YutResult result, Piece piece) {
        gameService.handleYutThrow(result);                          // 윷 결과를 큐에 적재
        game.setState(new SelectingPieceState(game, result)); // 상태 수동 전환
        gameService.handlePieceSelect(piece);                        // 실제 이동 수행
    }


    /* 테스트 케이스 1: 다른 사용자의 말을 잡는 경우
     * 잡힌 말은 출발지로 잘 가는지
     * 잡은 player YutThrowing 기회가 한 번 더 주어지는 지
     */
    @Test
    void catchSingleOpponentPiece_givesBonusTurn() {
        Piece attacker = p2.getPieces().get(0);
        Piece victim   = p1.getPieces().get(0);

        throwAndSelect(YutResult.DO, victim);
        throwAndSelect(YutResult.DO, attacker);

        // 피해 말은 출발점으로
        assertEquals(0, victim.getPosition().getIndex(), "잡힌 말은 출발점으로 돌아가야 한다");

        // 추가 턴 : 상태가 WaitingForThrowState 로 돌아와 있어야 함
        assertInstanceOf(WaitingForThrowState.class, game.getState(),
                "잡았으므로 추가 턴 상태(WaitingForThrowState)여야 한다");
        assertEquals(p2, game.getTurnManager().currentPlayer(), "말을 잡았기에 Player2의 차례여야 한다");
    }


    /* 테스트 케이스 2: 다른 사용자의 업힌 말을 잡는 경우
     * 모든 잡힌 말이 출발지로 잘 가는지
     * 업힌 말들의 그룹화가 풀리는지
     */
    @Test
    void catchRiddenGroup_resetsBothPiecesAndUngroups() {
        Piece p1a = p1.getPieces().get(0);
        Piece p1b = p1.getPieces().get(1);
        Piece p2a = p2.getPieces().get(0);
        // 실행
        throwAndSelect(YutResult.GEOL, p1a);
        throwAndSelect(YutResult.DO, p2a);
        throwAndSelect(YutResult.GEOL, p1b);

        assertEquals(List.of(p1a,p1b), p1a.getGroup(), "그룹이 생겨야 함");

        throwAndSelect(YutResult.GAE, p2a);

        // 두 말 모두 출발점
        assertEquals(0, p1a.getPosition().getIndex(), "잡힌 첫 번째 말이 출발점으로 돌아가야 함");
        assertEquals(0, p1b.getPosition().getIndex(), "잡힌 두 번째 말도 출발점으로 돌아가야 함");

        // 그룹 해제 → 각 말의 group 은 자기 자신뿐
        assertEquals(List.of(p1a), p1a.getGroup(), "첫 번째 말 그룹이 해제되어야 함");
        assertEquals(List.of(p1b), p1b.getGroup(), "두 번째 말 그룹이 해제되어야 함");
    }
}