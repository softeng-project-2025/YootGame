package app;

import controller.GameController;
import view.swing.SwingView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingView view = new SwingView();
            GameController controller = new GameController(view);
            view.setController(controller); // 반드시 먼저 주입
            view.showGameSetupDialog();     // 이 시점에서 controller는 null이 아님
        });
    }
}