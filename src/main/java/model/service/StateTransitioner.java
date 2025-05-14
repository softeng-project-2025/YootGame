package model.service;

import model.Game;
import model.dto.MoveResult;
import model.dto.NextStateHint;
import model.state.GameOverState;
import model.state.GameState;
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
        NextStateHint hint = result.getNextStateHint();
        if (hint == null) return;
        GameState nextState = StateFactory.create(hint, game);
        game.transitionTo(nextState);
    }
}

