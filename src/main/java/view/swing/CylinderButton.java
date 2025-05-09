package view.swing;

import model.position.Position;

import javax.swing.*;
import java.awt.*;

public class CylinderButton extends JButton {
    Position pos;

    public CylinderButton(Color color, Position pos) {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setPreferredSize(new Dimension(30, 20));
        setBackground(color);
        this.pos = pos;

        int x = pos.getX() - getWidth() / 2;
        int y = pos.getY() - getHeight() / 2;
        setBounds(x, y, getWidth(), getHeight());
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int r = h / 2;

        g2.setColor(getBackground().darker());
        g2.fillOval(pos.getX() - w / 2, pos.getY() + h / 2 - r / 2, w, r);
        g2.setColor(Color.BLACK);
        g2.drawOval(pos.getX() - w / 2, pos.getY() + h / 2 - r / 2, w, r);

        g2.setColor(getBackground().darker());
        g2.fillRect(pos.getX() - w / 2, pos.getY() - h / 2, w, h);
        g2.setColor(Color.BLACK);
        g2.drawRect(pos.getX() - w / 2, pos.getY() - h / 2, w, h);

        g2.setColor(getBackground().brighter());
        g2.fillOval(pos.getX() - w / 2, pos.getY() - h / 2 - r / 2, w, r);
        g2.setColor(Color.BLACK);
        g2.drawOval(pos.getX() - w / 2, pos.getY() - h / 2 - r / 2, w, r);

        g2.dispose();
    }
}
