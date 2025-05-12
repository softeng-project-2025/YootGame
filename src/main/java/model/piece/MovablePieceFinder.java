package model.piece;

import model.player.Player;
import model.yut.YutResult;

import java.util.List;

public interface MovablePieceFinder {
    List<Piece> findMovable(Player player, YutResult lastResult);
}