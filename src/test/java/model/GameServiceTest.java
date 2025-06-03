package model;

import model.dto.MoveFailType;
import model.dto.MoveResult;
import model.dto.NextStateHint;
import model.manager.TurnManager;
import model.piece.Piece;
import model.player.Player;
import model.service.GameService;
import model.state.*;
import model.yut.YutResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * GameServiceTest.java
 *
 * < 시나리오 목록 >
 *  1) 게임이 끝났지만 윷을 던질 때
 *  2) 윷을 던질 수 없는 상태에서 윷을 던지려 할 때
 *  3) 게임이 끝났지만 말을 선택하려 할 때
 *  4) 말 선택 state가 아닐 때 말을 선택하려 할 때
 *  5) turn 을 잘 인식하는가
 *  6) 현재 Game 을 잘 인식하는가
 *  7) 윷 결과가 항상 올바른가
 */
class GameServiceTest {

    private GameService sut;
    private model.Game mockGame;

    @BeforeEach
    void setUp() {
        mockGame = mock(model.Game.class);
        sut = new GameService(mockGame);
    }

    @Test @DisplayName("게임이 끝났지만 윷을 던질 때")
    void throwYut_whenGameFinished_returnsGameOver() {
        // 1) 게임이 끝난 상태로 스텁
        when(mockGame.isFinished()).thenReturn(true);

        // 2) getTurnManager()가 null을 내보내지 않도록 TurnManager 모킹
        TurnManager mockTm = mock(TurnManager.class);
        Player mockPlayer = mock(Player.class);
        when(mockTm.currentPlayer()).thenReturn(mockPlayer);
        when(mockGame.getTurnManager()).thenReturn(mockTm);

        // 3) 실제 호출
        MoveResult result = sut.throwYut(YutResult.MO);

        // 4) 검증
        assertTrue(result.isGameOver(), "게임 종료 상태면 isGameOver()가 true여야 한다");
    }

    @Test @DisplayName("윷을 던질 수 없는 상태에서 윷을 던지려 할 때")
    void throwYut_whenNotInThrowState_returnsThrowRequiredFailure() {
        // 1) 게임이 아직 끝나지 않은 상태
        when(mockGame.isFinished()).thenReturn(false);

        // 2) Throw 상태가 아닌 더미 상태(mock of GameState)
        GameState dummy = mock(GameState.class);
        when(mockGame.getState()).thenReturn(dummy);

        // 3) getTurnManager()도 stub
        TurnManager tm = mock(TurnManager.class);
        Player player = mock(Player.class);
        when(tm.currentPlayer()).thenReturn(player);
        when(mockGame.getTurnManager()).thenReturn(tm);

        // 4) 실제 호출
        MoveResult result = sut.throwYut(YutResult.DO);

        // 5) 검증: 실패, THROW_REQUIRED
        assertTrue(result.isFailure());
        assertEquals(MoveFailType.THROW_REQUIRED, result.failType());
    }



    @Test @DisplayName("게임이 끝났지만 말을 선택하려 할 때")
    void selectPiece_whenGameFinished_returnsGameOver() {
        when(mockGame.isFinished()).thenReturn(true);

        // TurnManager stub
        TurnManager tm = mock(TurnManager.class);
        Player p = mock(Player.class);
        when(tm.currentPlayer()).thenReturn(p);
        when(mockGame.getTurnManager()).thenReturn(tm);

        MoveResult result = sut.selectPiece(mock(Piece.class), YutResult.DO);

        assertTrue(result.isGameOver());
        verify(mockGame, never()).getState();
    }

    @Test @DisplayName("말 선택 state가 아닐 때 말을 선택하려 할 때")
    void selectPiece_whenNotInSelectState_returnsInvalidSelection() {
        when(mockGame.isFinished()).thenReturn(false);

        // 상태를 CanSelectPiece가 아닌 더미로 설정
        GameState dummy = mock(GameState.class);
        when(mockGame.getState()).thenReturn(dummy);

        // TurnManager stub (fail path에도 player 정보 필요)
        TurnManager tm = mock(TurnManager.class);
        Player p = mock(Player.class);
        when(tm.currentPlayer()).thenReturn(p);
        when(mockGame.getTurnManager()).thenReturn(tm);

        MoveResult result = sut.selectPiece(mock(Piece.class), YutResult.YUT);

        assertTrue(result.isFailure());
        assertEquals(MoveFailType.INVALID_SELECTION, result.failType());
    }

    @Test @DisplayName("turn 을 잘 인식하는가")
    void startTurn_callsGameStartTurn() {
        sut.startTurn();
        verify(mockGame).startTurn();
    }

    @Test @DisplayName("현재 Game 을 잘 인식하는가")
    void getGame_returnsOriginalGame() {
        assertSame(mockGame, sut.getGame());
    }

    @Test @DisplayName("윷 결과가 항상 올바른가")
    void randomThrowYut_generatesOnlyValidYutResults() {
        Set<YutResult> seen = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            MoveResult r = sut.throwYut();
            assertNotNull(r.yutResult());
            seen.add(r.yutResult());
        }
        // 6가지 중 최소 대부분이 나오는지 간단히 체크
        assertTrue(seen.containsAll(List.of(YutResult.DO, YutResult.GAE, YutResult.GEOL, YutResult.YUT, YutResult.MO, YutResult.BACK_DO)));
    }





}