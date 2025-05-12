package app;

import controller.GameController;
import view.swing.SwingView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingView view = new SwingView();
            GameController ctrl = new GameController(view);
        });
    }
}
