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
        java.util.List<model.position.Position> allVertexPositions = pathStrategy.getAllVertexPositions();

        int marginX = 70;
        int marginY = (getHeight() - 600) / 2;

        for(int i=0; i<allVertexPositions.size(); i+=2) {
            model.position.Position position1 = allVertexPositions.get(i);
            model.position.Position position2 = allVertexPositions.get(i+1);

            g2.drawLine(position1.getX() + marginX, position1.getY() + marginY, position2.getX() + marginX, position2.getY() + marginY);
        }

        for(model.position.Position position : allPositions) {
            if (position.isCenter() || position.isDiagonalEntry()) {
                drawNoon(position, g2, 30);
            }
            drawNoon(position, g2, 20);
        }
    }

    private void drawNoon(model.position.Position position, Graphics2D g2, int r) {
        int marginX = 70;
        int marginY = (getHeight() - 600) / 2;

        int x = position.getX() + marginX - r;
        int y = position.getY() + marginY - r;

        g2.setColor(Color.WHITE);
        g2.fillOval(x, y, r * 2, r * 2);
        g2.setColor(Color.BLACK);
        g2.drawOval(x, y, r * 2, r * 2);
    }
}

