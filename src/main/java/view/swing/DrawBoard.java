package view.swing;

import model.strategy.PathStrategy;

import javax.swing.*;
import java.awt.*;

public class DrawBoard extends JPanel {
    private final PathStrategy pathStrategy;    // final이 될 수 있다고 해서 일단 붙였는데 문제 생기면 여기 보기

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

        // draw line
        for(int i=0; i<allVertexPositions.size(); i+=2) {
            model.position.Position p1 = allVertexPositions.get(i);
            model.position.Position p2 = allVertexPositions.get(i+1);

            int x1 = p1.x() + marginX;
            int y1 = p1.y() + marginY;
            int x2 = p2.x() + marginX;
            int y2 = p2.y() + marginY;

            g2.drawLine(x1, y1, x2, y2);
        }

        // draw noon
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

        int x = position.x() + marginX - r;
        int y = position.y() + marginY - r;

        g2.setColor(Color.WHITE);
        g2.fillOval(x, y, r * 2, r * 2);
        g2.setColor(Color.BLACK);
        g2.drawOval(x, y, r * 2, r * 2);
    }
}

