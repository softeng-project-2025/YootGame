package view;

import controller.GameController;
import model.yut.YutResult;

public interface View {
    void showMessage(String message);
    void setController(controller.GameController controller);
    void renderGame(model.Game game);
    void updateYutResult(YutResult yutResult);
    void promptRestart(GameController controller);
}