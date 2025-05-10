package view;

import controller.GameController;
import model.dto.MessageType;
import model.dto.MoveResult;
import model.piece.Piece;
import model.player.Player;
import model.yut.YutResult;

import java.util.List;

public interface View {
    void showMessage(String message);
    void setController(controller.GameController controller);
    void renderGame(model.Game game);
    void render(MoveResult moveResult);
    void updateYutResult(YutResult yutResult);
    void promptRestart(GameController controller);
    void showWinner(Player currentPlayer);
    void updateStatus(String message);
    void updateStatus(String message, MessageType type);
    void showSelectablePieces(List<Piece> pieces);
}