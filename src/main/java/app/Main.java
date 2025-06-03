package app;

import controller.GameController;
import view.swing.SwingView;
import javafx.application.Application;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
//        new Main().SwingMain();
        new Main().JavafxMain(args);
    }

    private void JavafxMain(String[] args) {
        Application.launch(Launcher.class, args);
    }

    private void SwingMain() {
        SwingUtilities.invokeLater(() -> {
            SwingView view = new SwingView();
            GameController ctrl = new GameController(view);
        });
    }
}