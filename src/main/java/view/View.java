package view;

public interface View {
    void showMessage(String message);
    void setController(controller.GameController controller);
    void renderGame(model.Game game);
}