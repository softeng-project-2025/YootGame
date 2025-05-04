package view;

import model.yut.YutResult;

public interface View {
    void showMessage(String message);
    void setController(controller.GameController controller);
    void renderGame(model.Game game);
    void updateYutResult(YutResult yutResult);
}