package app;

import controller.GameController;
import javafx.application.Application;
import javafx.stage.Stage;
import view.javafx.JavafxView;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) {
        JavafxView view = new JavafxView();
        new GameController(view);
    }
}