package view.swing;

import model.position.Position;

import javax.swing.*;
import java.awt.*;

public class CylinderButton extends JButton {

    /* 버튼(실린더) 자체 크기 */
    private static final int BODY_W   = 60;   // 실린더 넓이
    private static final int BODY_H   = 20;   // 실린더 높이
    private static final int CAP_H    = 20;   // 타원(뚜껑) 높이

    /* DrawBoard 와 동일한 여백 상수 */
    private static final int BOARD_W  = 600;  // 보드 세로 영역(픽셀)
    private static final int MARGIN_X = 70;

    private Position pos;                     // 게임 상의 논리적 위치

    public CylinderButton(Color color, Position pos, String text) {
        super(text);

        this.pos = pos;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.BLACK);
        setFont(new Font("맑은 고딕", Font.BOLD, 13));
        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.CENTER);

        /* ← 1 px 여백을 두어 외곽선이 끊기지 않도록 +2 */
        int prefW = BODY_W  + 2;
        int prefH = BODY_H + CAP_H + 2;
        setPreferredSize(new Dimension(prefW, prefH));

        setBackground(color);
        updateBounds();
    }

    /* Piece 의 논리 좌표가 바뀔 때 호출 */
    public void setPosition(Position pos) {
        this.pos = pos;
        updateBounds();
        repaint();
    }

    /* 화면 좌표로 변환하여 버튼 배치 */
    private void updateBounds() {
        /* DrawBoard 와 동일한 margin 계산 */
        int marginY = 0;
        Container p = getParent();
        if (p != null) {
            marginY = (p.getHeight() - BOARD_W) / 2;
        }

        int uiX = pos.x() + MARGIN_X - BODY_W / 2 - 1;          // -1 : 1 px 여백
        int uiY = pos.y() + marginY  - BODY_H / 2 - CAP_H / 2 - 1;

        int w = BODY_W + 2;                 // 1 px 여백 × 2
        int h = BODY_H + CAP_H + 2;
        setBounds(uiX, uiY, w, h);
    }

    /* 실린더 커스텀 페인팅 */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        /* 1 px 안쪽으로 이동하여 여백 확보 */
        g2.translate(1, 1 + CAP_H / 2);

        /* 크기 계산 ‑ 여백을 뺀 실제 그릴 영역 */
        int w = BODY_W;
        int h = BODY_H;

        // 하단 타원
        g2.setColor(getBackground().darker());
        g2.fillOval(0, h - CAP_H / 2, w, CAP_H);
        g2.setColor(Color.BLACK);
        g2.drawOval(0, h - CAP_H / 2, w, CAP_H);

        // 몸통
        g2.setColor(getBackground().darker());
        g2.fillRect(0, 0, w, h);
        g2.setColor(Color.BLACK);
        g2.drawLine(0, 0, 0, h);
        g2.drawLine(w, 0, w, h);

        // 상단 타원
        g2.setColor(getBackground());
        g2.fillOval(0, -CAP_H / 2, w, CAP_H);
        g2.setColor(Color.BLACK);
        g2.drawOval(0, -CAP_H / 2, w, CAP_H);

        // 라벨(텍스트)
        FontMetrics fm = g2.getFontMetrics();
        String txt = getText();
        int txtX = (w - fm.stringWidth(txt)) / 2;
        int txtY = (h + fm.getAscent() * 2) / 2;
        g2.setColor(getForeground());
        g2.drawString(txt, txtX, txtY);

        g2.dispose();
    }

    /* 부모가 정해진 뒤(화면에 추가된 뒤) marginY 재계산 */
    @Override
    public void addNotify() {
        super.addNotify();
        updateBounds();
    }
}