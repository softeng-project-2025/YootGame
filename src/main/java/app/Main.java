package app;

import controller.GameController;
import view.swing.SwingView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingView view = new SwingView();
            GameController ctrl = new GameController(view);
            view.showGameSetupDialog();  // 여기서 OK 시 ctrl.initializeGame(...) 호출
        });
    }
}
