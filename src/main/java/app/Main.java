package app;

import controller.GameController;
import view.swing.SwingView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
            SwingView view = new SwingView(controller);
            view.showGameSetupDialog();
        });

    }
}