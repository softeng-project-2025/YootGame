package model.state;

import model.Game;

public interface GameState {
    /** 상태 진입 시 호출 */
    default void onEnter(Game game) {}

    /** 상태 종료 시 호출 */
    default void onExit(Game game) {}
}