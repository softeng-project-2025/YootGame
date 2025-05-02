package model.piece;

import model.player.Player;
import model.position.Position;

import java.util.ArrayList;
import java.util.List;

public class Piece {
    private final Player owner;
    private final int id;
    private Position position;
    private boolean isFinished = false;
    private boolean hasMoved = false;
    private List<Piece> group;

    public Piece(Player owner, int id, Position startPos) {
        this.owner = owner;
        this.id = id;
        this.position = startPos;
        this.group = new ArrayList<>(List.of(this)); // 단독 그룹 초기화
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

    public boolean isGrouped() {
        return this.getGroup().size() > 1;
    }
}