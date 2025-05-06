package model.dto;

import model.Game;
import model.player.Player;

public record MoveResult(
        boolean captured,
        boolean gameEnded,
        Player winner,
        boolean bonusTurn,
        boolean turnSkipped
) {
    public static MoveResult fail() {
        return new MoveResult(false, false, null, false, false);
    }

    public static MoveResult goal(Player winner, Game game) {
        return new MoveResult(false, game.isFinished(), winner, false, false);
    }

    public static MoveResult success(boolean captured, boolean bonusTurn, Player winner, Game game) {
        return new MoveResult(captured, game.isFinished(), winner, bonusTurn, false);
    }

    public static MoveResult skipped() {
        return new MoveResult(false, false, null, false, true);
    }

}