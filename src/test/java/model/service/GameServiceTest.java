package model.service;

import model.Game;
import model.dto.MoveFailType;
import model.dto.MoveResult;
import model.dto.NextStateHint;
import model.manager.TurnManager;
import model.piece.MovablePieceFinder;
import model.piece.Piece;
import model.player.Player;
import model.state.*;
import model.turn.TurnResult;
import model.yut.YutResult;
import model.yut.YutThrower;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

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
    void throwYut_whenInThrowState_appliesStateTransitionAndReturnsResult() {
        // 1) 게임이 아직 끝나지 않은 상태로 스텁
        when(mockGame.isFinished()).thenReturn(false);

        // 2) CanThrowYut 상태를 Mockito로 스텁
        CanThrowYut throwState = mock(CanThrowYut.class);
        when(mockGame.getState()).thenReturn(throwState);

        // 3) MoveResult를 mock 객체로 생성하고, nextStateHint도 스텁
        MoveResult mockResult = mock(MoveResult.class);
        when(throwState.handleYutThrow(YutResult.YUT)).thenReturn(mockResult);
        when(mockResult.isFailure()).thenReturn(false);  // 실패 플래그는 false
        when(mockResult.nextStateHint()).thenReturn(NextStateHint.WAITING_FOR_THROW);

        // 4) 실제 호출
        MoveResult actual = sut.throwYut(YutResult.YUT);

        // 5) stub으로 지정한 객체가 그대로 반환되는지 확인
        assertSame(mockResult, actual);

        // 6) 이 hint 에 대응해 상태 전이 메서드가 호출되었는지 검증
        verify(mockGame).transitionTo(any());
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

    @Test
    void selectPiece_whenInSelectState_appliesStateTransitionAndReturnsResult() {
        when(mockGame.isFinished()).thenReturn(false);

        // CanSelectPiece 상태 mock
        CanSelectPiece selState = mock(CanSelectPiece.class);
        when(mockGame.getState()).thenReturn(selState);

        // 실제 반환할 MoveResult mock
        MoveResult mockResult = mock(MoveResult.class);
        when(selState.handlePieceSelect(any(Piece.class), eq(YutResult.MO)))
                .thenReturn(mockResult);
        when(mockResult.isFailure()).thenReturn(false);
        when(mockResult.nextStateHint()).thenReturn(model.dto.NextStateHint.WAITING_FOR_THROW);

        MoveResult actual = sut.selectPiece(mock(Piece.class), YutResult.MO);

        assertSame(mockResult, actual);
        // 상태 전이 호출 확인
        verify(mockGame).transitionTo(any());
    }

    // 1) NEXT_TURN 분기
    @Test
    void throwYut_whenNextTurn_thenAdvancesTurnAndTransitions() {
        // 1) 게임이 아직 끝나지 않은 상태
        when(mockGame.isFinished()).thenReturn(false);

        // 2) CanThrowYut 상태를 Mockito로 스텁
        CanThrowYut state = mock(CanThrowYut.class);
        when(mockGame.getState()).thenReturn(state);

        // 3) TurnManager도 모킹해서 getTurnManager()가 null을 내보내지 않도록
        TurnManager tm = mock(TurnManager.class);
        when(mockGame.getTurnManager()).thenReturn(tm);

        // 4) MoveResult mock 및 hint 설정
        MoveResult r = mock(MoveResult.class);
        when(state.handleYutThrow(YutResult.YUT)).thenReturn(r);
        when(r.isFailure()).thenReturn(false);
        when(r.nextStateHint()).thenReturn(NextStateHint.NEXT_TURN);

        // 5) 실제 호출
        sut.throwYut(YutResult.YUT);

        // 6) 검증: startTurn(), nextTurn(), transitionTo(WaitingForThrowState) 호출
        verify(mockGame).startTurn();
        verify(tm).nextTurn();
        verify(mockGame).transitionTo(isA(WaitingForThrowState.class));
    }

    // 2) GAME_ENDED 분기
    @Test
    void throwYut_whenGameEnded_thenTransitionToGameOver() {
        when(mockGame.isFinished()).thenReturn(false);

        CanThrowYut state = mock(CanThrowYut.class);
        when(mockGame.getState()).thenReturn(state);

        MoveResult r = mock(MoveResult.class);
        when(state.handleYutThrow(YutResult.YUT)).thenReturn(r);
        when(r.isFailure()).thenReturn(false);
        when(r.nextStateHint()).thenReturn(NextStateHint.GAME_ENDED);

        sut.throwYut(YutResult.YUT);

        verify(mockGame).transitionTo(isA(GameOverState.class));
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

    // 4) getSelectablePieces 위임 테스트
    @Test
    void getSelectablePieces_delegatesToFinder() {
        // Arrange
        TurnManager tm = mock(TurnManager.class);
        Player player = mock(Player.class);
        when(tm.currentPlayer()).thenReturn(player);
        when(mockGame.getTurnManager()).thenReturn(tm);

        // Stub TurnResult
        TurnResult turnResult = mock(TurnResult.class);
        when(turnResult.hasPending()).thenReturn(true);            // <— add this
        when(turnResult.getLastResult()).thenReturn(YutResult.MO);
        when(mockGame.getTurnResult()).thenReturn(turnResult);

        // Stub finder
        List<Piece> expected = List.of(mock(Piece.class));
        MovablePieceFinder stubFinder = mock(MovablePieceFinder.class);
        when(stubFinder.findMovable(player, YutResult.MO))
                .thenReturn(expected);

        sut = new GameService(mockGame, stubFinder);

        // Act
        List<Piece> actual = sut.getSelectablePieces();

        // Assert
        assertSame(expected, actual);
    }

    @Test
    void selectPiece_whenNextTurn_thenAdvancesTurnAndTransitions() {
        // 준비
        when(mockGame.isFinished()).thenReturn(false);
        CanSelectPiece selState = mock(CanSelectPiece.class);
        when(mockGame.getState()).thenReturn(selState);

        TurnManager tm = mock(TurnManager.class);
        when(mockGame.getTurnManager()).thenReturn(tm);

        MoveResult r = mock(MoveResult.class);
        when(selState.handlePieceSelect(any(Piece.class), eq(YutResult.MO)))
                .thenReturn(r);
        when(r.isFailure()).thenReturn(false);
        when(r.nextStateHint()).thenReturn(NextStateHint.NEXT_TURN);

        // 실행
        sut.selectPiece(mock(Piece.class), YutResult.MO);

        // 검증
        verify(mockGame).startTurn();
        verify(tm).nextTurn();
        verify(mockGame).transitionTo(isA(WaitingForThrowState.class));
    }

    @Test
    void selectPiece_whenGameEnded_thenTransitionToGameOver() {
        when(mockGame.isFinished()).thenReturn(false);
        CanSelectPiece selState = mock(CanSelectPiece.class);
        when(mockGame.getState()).thenReturn(selState);

        MoveResult r = mock(MoveResult.class);
        when(selState.handlePieceSelect(any(), any())).thenReturn(r);
        when(r.isFailure()).thenReturn(false);
        when(r.nextStateHint()).thenReturn(NextStateHint.GAME_ENDED);

        sut.selectPiece(mock(Piece.class), YutResult.DO);

        verify(mockGame).transitionTo(isA(GameOverState.class));
    }

    @Test
    void selectPiece_whenStay_thenDoesNotTransition() {
        when(mockGame.isFinished()).thenReturn(false);
        CanSelectPiece selState = mock(CanSelectPiece.class);
        when(mockGame.getState()).thenReturn(selState);

        MoveResult r = mock(MoveResult.class);
        when(selState.handlePieceSelect(any(), any())).thenReturn(r);
        when(r.isFailure()).thenReturn(false);
        when(r.nextStateHint()).thenReturn(NextStateHint.STAY);

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
    void randomThrowYut_delegatesToYutThrowerAndReturnsValidResult() {
        // 1) mockGame.getTurnResult() 가 null 을 리턴하지 않도록 미리 준비
        TurnResult fakeTr = new TurnResult();
        when(mockGame.getTurnResult()).thenReturn(fakeTr);

        // 2) GameState 와 TurnManager 도 stub (CanThrowYut 이어야 내부 분기 통과)
        CanThrowYut throwState = mock(CanThrowYut.class);
        when(mockGame.getState()).thenReturn(throwState);
        when(mockGame.isFinished()).thenReturn(false);

        // 3) YutThrower.throwYut() 이 MO 를 반환하도록 mocking
        try (MockedStatic<YutThrower> mockYut = mockStatic(YutThrower.class)) {
            mockYut.when(YutThrower::throwYut).thenReturn(YutResult.MO);

            // 4) handleYutThrow 에도 MoveResult 를 리턴하도록 stub
            MoveResult expected = MoveResult.success(
                    YutResult.MO,
                    /*captured*/ false,
                    /*bonusTurn*/ false,
                    /*winner*/ null,
                    /*game*/ mockGame
            );
            when(throwState.handleYutThrow(YutResult.MO)).thenReturn(expected);

            // 5) 실제 호출
            MoveResult actual = sut.throwYut();

            assertSame(expected, actual);
            // pending 에도 잘 기록됐는지
            assertTrue(fakeTr.hasPending());
        }
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