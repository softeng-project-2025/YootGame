package model.piece;

import model.player.Player;
import model.position.Position;

import java.util.ArrayList;
import java.util.List;

public class Piece {
    private Player owner;
    private int id;
    private Position position;
    private boolean isGrouped;
    private boolean isFinished;
    private boolean hasMoved = false;
    private List<Piece> group = new ArrayList<>();

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

    public List<Piece> getGroup() {
        return group;
    }

    public void setGroup(List<Piece> group) {
        this.group = group;
    }

    public void setMoved() {
        this.hasMoved = true;
    }
    public boolean hasMoved() {
        return this.hasMoved;
    }
}