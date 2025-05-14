package model.service;

import model.dto.MoveFailType;
import model.dto.MoveResult;
import model.dto.NextStateHint;
import model.manager.TurnManager;
import model.piece.Piece;
import model.player.Player;
import model.state.*;
import model.yut.YutResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private GameService sut;
    private model.Game mockGame;

    @BeforeEach
    void setUp() {
        mockGame = mock(model.Game.class);
        sut = new GameService(mockGame);
    }

    @Test
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

    @Test
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



    @Test
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

    @Test
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

    // 3) 실패 분기
    @Test
    void throwYut_whenResultFailure_doesNotTransition() {
        when(mockGame.isFinished()).thenReturn(false);

        CanThrowYut state = mock(CanThrowYut.class);
        when(mockGame.getState()).thenReturn(state);

        MoveResult r = mock(MoveResult.class);
        when(state.handleYutThrow(YutResult.YUT)).thenReturn(r);
        when(r.isFailure()).thenReturn(true);

        sut.throwYut(YutResult.YUT);

        // applyNextState 내부 어디에도 닿으면 안 됨
        verify(mockGame, never()).transitionTo(any());
        verify(mockGame, never()).startTurn();
    }

    @Test
    void selectPiece_whenStay_thenDoesNotTransition() {
        when(mockGame.isFinished()).thenReturn(false);
        CanSelectPiece selState = mock(CanSelectPiece.class);
        when(mockGame.getState()).thenReturn(selState);

        MoveResult r = mock(MoveResult.class);
        when(selState.handlePieceSelect(any(), any())).thenReturn(r);
        when(r.isFailure()).thenReturn(false);
        when(r.nextStateHint()).thenReturn(NextStateHint.SELECTING_PIECE);

        sut.selectPiece(mock(Piece.class), YutResult.YUT);

        verify(mockGame, never()).transitionTo(any());
        verify(mockGame, never()).startTurn();
    }

    @Test
    void startTurn_callsGameStartTurn() {
        sut.startTurn();
        verify(mockGame).startTurn();
    }

    @Test
    void getGame_returnsOriginalGame() {
        assertSame(mockGame, sut.getGame());
    }

    @Test
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