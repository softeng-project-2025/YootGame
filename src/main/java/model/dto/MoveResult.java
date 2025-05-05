package model.dto;

import model.player.Player;

public class MoveResult {
    private final String message;
    private final boolean captured;
    private final boolean gameFinished;
    private final Player winner;
    private final boolean bonusTurn;

    public MoveResult(String message, boolean captured, boolean gameFinished, Player winner, boolean bonusTurn) {
        this.message = message;
        this.captured = captured;
        this.gameFinished = gameFinished;
        this.winner = winner;
        this.bonusTurn = bonusTurn;
    }

    public String getMessage() { return message; }
    public boolean isCaptured() { return captured; }
    public boolean isGameFinished() { return gameFinished; }
    public Player getWinner() { return winner; }
    public boolean isBonusTurn() { return bonusTurn; }
}