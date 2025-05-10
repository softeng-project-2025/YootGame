package view;

import controller.GameController;
import model.dto.MessageType;
import model.dto.MoveResult;
import model.piece.Piece;
import model.player.Player;
import model.strategy.PathStrategy;
import model.yut.YutResult;

import java.util.List;

public interface View {

    void setController(controller.GameController controller);
    void renderGame(Object dtoObj);

    void promptRestart(GameController controller);
    void showWinner(Player currentPlayer);

    void updateStatus(String message, MessageType type);
    void showSelectablePieces(List<Piece> pieces);

    void showGameSetupDialog();

    void showMessage(String s);
}