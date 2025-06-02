package model;

import model.Game;
import model.dto.MoveResult;
import model.dto.NextStateHint;
import model.manager.TurnManager;
import model.player.Player;
import model.service.StateFactory;
import model.yut.YutResult;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TurnTest.java
 *
 * < 시나리오 목록 >
 *  1) NEXT_TURN 힌트가 나오면 TurnManager 가 다음 플레이어로 넘어간다.
 *  2) 윷/모를 던져 bonusTurn == true 인 경우 WAITING_FOR_THROW → 같은 플레이어가 다시 던진다.
 *  3) DO·GAE·GEOL 로 상대 말을 잡으면 bonusTurn == true → WAITING_FOR_THROW → 같은 플레이어가 이어서 던진다.
 *  4) YUT 로 상대 말을 잡을 땐 bonusTurn 이 추가 발생하지 않으므로 NEXT_TURN → 턴이 넘어간다.
 *  5) BACK_DO 스킵(첫 move 전, pending 없을 때) 은 NEXT_TURN → 턴이 넘어간다.
 */
class TurnTest {

    /** 공통 유틸 : 2인 TurnManager 와 Mockito Game 더블을 만든다. */
    private static Bundle newBundle() {
        // 플레이어 세팅
        Player p1 = new Player(1, "Alice", 2);
        Player p2 = new Player(2, "Bob",   2);
        TurnManager tm = new TurnManager(List.of(p1, p2));

        // Game 은 Mockito 로 최소 동작만 스텁
        Game game = mock(Game.class);
        when(game.getTurnManager()).thenReturn(tm);
        when(game.isFinished()).thenReturn(false);
        doNothing().when(game).startTurn();

        return new Bundle(tm, game, p1, p2);
    }

    private record Bundle(TurnManager tm, Game game, Player p1, Player p2) {}

    @Test @DisplayName("① NEXT_TURN → 턴 교체")
    void nextTurnSwitches() {
        Bundle b = newBundle();

        // NEXT_TURN 힌트 강제
        StateFactory.create(NextStateHint.NEXT_TURN, b.game());

        assertEquals(b.p2(), b.tm().currentPlayer(), "플레이어가 p2 로 넘어가야 한다");
    }

    @Test @DisplayName("② 윷/모 보너스 → 턴 유지")
    void bonusTurnFromYutKeepsPlayer() {
        Bundle b = newBundle();

        // 윷 보너스 MoveResult (bonusTurn = true, hasPending = true)
        MoveResult m = MoveResult.success(
                YutResult.YUT, false, true, null,
                b.game()
        );
        StateFactory.create(m.getNextStateHint(), b.game());

        assertEquals(b.p1(), b.tm().currentPlayer(), "보너스턴: p1 이 계속 진행");
    }

    @Test @DisplayName("③ DO 로 캡처 → 보너스턴 → 턴 유지")
    void captureWithDoGivesBonusTurn() {
        Bundle b = newBundle();

        MoveResult m = MoveResult.success(
                YutResult.DO, true, true, null,
                b.game(), null, false, Map.of(), Map.of()
        );
        StateFactory.create(m.getNextStateHint(), b.game());

        assertEquals(b.p1(), b.tm().currentPlayer(), "캡처 보너스턴: p1 유지");
    }

    @Test @DisplayName("④ YUT 로 캡처 → 추가 보너스 없음 → 턴 교체")
    void captureWithYutNoExtra() {
        Bundle b = newBundle();

        MoveResult m = MoveResult.success(
                YutResult.YUT, true, false, null,
                b.game(), null, false, Map.of(), Map.of()
        );
        StateFactory.create(m.getNextStateHint(), b.game());

        assertEquals(b.p2(), b.tm().currentPlayer(), "추가 보너스 없음: 턴이 p2 로 넘어감");
    }

    @Test @DisplayName("⑤ BACK_DO 스킵 → NEXT_TURN → 턴 교체")
    void skippedBackDoSwitchesTurn() {
        Bundle b = newBundle();

        MoveResult m = MoveResult.skipped(YutResult.BACK_DO);
        StateFactory.create(m.getNextStateHint(), b.game());

        assertEquals(b.p2(), b.tm().currentPlayer(), "스킵 시 턴이 p2 로 넘어가야 한다");
    }
}
