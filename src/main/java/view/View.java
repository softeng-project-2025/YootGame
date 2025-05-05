package view;

import controller.GameController;
import model.player.Player;
import model.yut.YutResult;

public interface View {
    void showMessage(String message);
    void setController(controller.GameController controller);
    void renderGame(model.Game game);
    void updateYutResult(YutResult yutResult);
    void promptRestart(GameController controller);
    void showWinner(Player currentPlayer);
}