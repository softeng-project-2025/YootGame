package model.piece;

import model.player.Player;
import model.yut.YutResult;

import java.util.List;

public class DefaultMovablePieceFinder implements MovablePieceFinder {
    @Override
    public List<Piece> findMovable(Player player, YutResult lastResult) {
        return PieceUtil.getMovablePieces(player, lastResult);
    }

}
