package view.swing;

import model.strategy.PathStrategy;

import javax.swing.*;
import java.awt.*;

public class DrawBoard extends JPanel {
    PathStrategy pathStrategy;

    DrawBoard(PathStrategy pathStrategy) {
        this.pathStrategy = pathStrategy;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        java.util.List<model.position.Position> allPositions = pathStrategy.getAllPositions();

        for(model.position.Position position : allPositions) {
            int x = position.getX();
            int y = position.getY();
            int r = (position.isCenter() || position.isDiagonalEntry()) ? 30 : 20;

            g2.fillOval(x - r, y - r, r * 2, r * 2);
        }
    }
}

