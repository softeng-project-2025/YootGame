package model.service;

import model.Game;
import model.dto.NextStateHint;
import model.state.GameOverState;
import model.state.GameState;
import model.state.SelectingPieceState;
import model.state.WaitingForThrowState;


public class StateFactory {
    public static GameState create(NextStateHint hint, Game game) {
        switch (hint) {
            case WAITING_FOR_THROW:
                // bonusTurn 인 경우에만 진짜 던지기 대기 상태로
                return new WaitingForThrowState(game);

            case SELECTING_PIECE:
                // 같은 플레이어가 pending 을 가지고 계속 선택 단계로
                return new SelectingPieceState(game);

            case NEXT_TURN:
                // 턴 넘기기
                game.startTurn();
                game.getTurnManager().nextTurn();
                return new WaitingForThrowState(game);

            case GAME_ENDED:
                return new GameOverState();

            default:
                // 알 수 없는 힌트의 경우 예외 처리
                throw new IllegalArgumentException("알 수 없는 상태 힌트: " + hint);
        }

    }

}

