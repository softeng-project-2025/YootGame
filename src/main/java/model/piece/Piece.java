package model.piece;

import model.player.Player;
import model.position.Position;

public class Piece {
    private Player owner;
    private int id;
    private Position position;
    private boolean isGrouped;
    private boolean isFinished;

    public Piece(Player owner, int id, Position startPos) {
        this.owner = owner;
        this.id = id;
        this.position = startPos;
        this.isGrouped = false;
        this.isFinished = false;
    }

    public Player getOwner() {
        return owner;
    }

    public int getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isGrouped() {
        return isGrouped;
    }

    public void setGrouped(boolean grouped) {
        this.isGrouped = grouped;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        this.isFinished = finished;
    }
}