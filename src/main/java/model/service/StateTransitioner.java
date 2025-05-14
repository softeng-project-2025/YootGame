package model.service;

import model.Game;
import model.dto.MoveResult;
import model.dto.NextStateHint;
import model.state.GameOverState;
import model.state.SelectingPieceState;
import model.state.WaitingForThrowState;

public class StateTransitioner {
    private final Game game;

    public StateTransitioner(Game game) {
        this.game = game;
    }

    /**
     * MoveResult의 NextStateHint를 기반으로 game의 상태를 전이
     */
    public void transition(MoveResult result) {
        if (result == null || result.isFailure()) return;
        NextStateHint hint = result.nextStateHint();
        if (hint == null) return;
        switch (result.nextStateHint()) {
            case WAITING_FOR_THROW:
                // bonusTurn 인 경우에만 진짜 던지기 대기 상태로
                game.transitionTo(new WaitingForThrowState(game));
                break;

            case SELECTING_PIECE:
                // 같은 플레이어가 pending 을 가지고 계속 선택 단계로
                game.transitionTo(new SelectingPieceState(game));
                break;

            case NEXT_TURN:
                // 턴 넘기기
                game.startTurn();
                game.getTurnManager().nextTurn();
                game.transitionTo(new WaitingForThrowState(game));
                break;

            case GAME_ENDED:
                game.transitionTo(new GameOverState());
                break;
        }
    }
}

