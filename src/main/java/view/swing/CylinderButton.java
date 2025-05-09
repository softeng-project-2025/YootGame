package view.swing;

import model.position.Position;

import javax.swing.*;
import java.awt.*;

public class CylinderButton extends JButton {

    private static final int WIDTH  = 30;  // 버튼 실제 너비
    private static final int HEIGHT = 20;  // 전체 높이
    private static final int CAP_H  = 10;  // 상‧하 타원(겉보기 원) 높이

    private Position pos;                 // 말의 실시간 위치

    public CylinderButton(Color color, Position pos, String text) {
        super(text);                      // 라벨용 텍스트
        this.pos = pos;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.BLACK);
        setFont(new Font("맑은 고딕", Font.BOLD, 8));
        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.CENTER);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(color);

        updateBounds();
    }

    /* Position 이 변할 때 호출 */
    public void setPosition(Position pos) {
        this.pos = pos;
        updateBounds();
        repaint();
    }

    /* pos 정보를 기준으로 컴포넌트 위치 재계산 */
    private void updateBounds() {
        int x = pos.getX() - WIDTH / 2;
        int y = pos.getY() - HEIGHT / 2;
        setBounds(x, y, WIDTH, HEIGHT);
    }

    /* 실린더 커스텀 페인팅 */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int rectH = HEIGHT - CAP_H; // 몸통(직사각형) 높이

        // 하단 타원
        g2.setColor(getBackground().darker());
        g2.fillOval(0, rectH, WIDTH, CAP_H);
        g2.setColor(Color.BLACK);
        g2.drawOval(0, rectH, WIDTH, CAP_H);

        // 몸통
        g2.setColor(getBackground().darker());
        g2.fillRect(0, CAP_H / 2, WIDTH, rectH);
        g2.setColor(Color.BLACK);
        g2.drawRect(0, CAP_H / 2, WIDTH, rectH);

        // 상단 타원
        g2.setColor(getBackground().brighter());
        g2.fillOval(0, 0, WIDTH, CAP_H);
        g2.setColor(Color.BLACK);
        g2.drawOval(0, 0, WIDTH, CAP_H);

        // 라벨(텍스트)
        FontMetrics fm = g2.getFontMetrics();
        String txt = getText();
        int txtX = (WIDTH  - fm.stringWidth(txt)) / 2;
        int txtY = (HEIGHT + fm.getAscent() - fm.getDescent()) / 2 - 1;
        g2.setColor(getForeground());
        g2.drawString(txt, txtX, txtY);

        g2.dispose();
    }
}