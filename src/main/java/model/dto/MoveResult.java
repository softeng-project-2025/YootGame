package model.dto;

import model.player.Player;

public class MoveResult {
    private final boolean captured;
    private final boolean gameFinished;
    private final Player winner;
    private final boolean bonusTurn;
    private final boolean turnSkipped;


    public MoveResult(boolean captured, boolean gameFinished,
                      Player winner, boolean bonusTurn, boolean turnSkipped) {
        this.captured = captured;
        this.gameFinished = gameFinished;
        this.winner = winner;
        this.bonusTurn = bonusTurn;
        this.turnSkipped = turnSkipped;
    }

    public boolean isCaptured() { return captured; }
    public boolean isGameFinished() { return gameFinished; }
    public Player getWinner() { return winner; }
    public boolean isBonusTurn() { return bonusTurn; }
    public boolean isTurnSkipped() { return turnSkipped; }
}