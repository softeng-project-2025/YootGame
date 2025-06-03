package view.javafx;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import model.position.Position;

/**
 * CylinderButton – Java FX
 *  · Label 사용 대신 GraphicsContext 로 텍스트 직접 렌더링
 */
public class CylinderButton extends StackPane {

    /* 실린더 크기 */
    private static final double BODY_W = 60;
    private static final double BODY_H = 20;
    private static final double CAP_H  = 20;

    /* DrawBoard 와 동일한 여백 */
    private static final double MARGIN_X = 50;
    private static final double BOARD_W  = 600;

    private static final Font TEXT_FONT =
            Font.font("맑은 고딕", FontWeight.BOLD, 13);

    private final Canvas canvas = new Canvas(BODY_W + 2, BODY_H + CAP_H + 2);
    private Position pos;
    private final String label;

    public CylinderButton(Color color, Position pos, String text) {
        this.pos   = pos;
        this.label = text;

        getChildren().add(canvas);       // 캔버스만 보유
        setPickOnBounds(false);

        drawCylinder(color);

        /* 부모 / 크기 변화 감지하여 좌표 갱신 */
        ChangeListener<? super Number> l = (o, oldV, newV) -> updateBounds();
        parentProperty().addListener((o, oldP, newP) -> updateBounds());
        widthProperty().addListener(l);
        heightProperty().addListener(l);

        updateBounds();
    }

    /* 논리 좌표 변경 시 호출 */
    public void setPosition(Position pos) {
        this.pos = pos;
        updateBounds();
    }

    /*----------------- 내부 -----------------*/

    public void updateBounds() {
        if (pos == null) return;

        double marginY = 0;
        if (getParent() != null)
            marginY = (getParent().getBoundsInLocal().getHeight() - BOARD_W) / 2;

        double uiX = pos.x() + MARGIN_X - BODY_W / 2 - 1;
        double uiY = pos.y() + marginY - BODY_H / 2 - CAP_H / 2 - 1;
        relocate(uiX, uiY);
    }

    /** 실린더 + 텍스트 그리기 */
    private void drawCylinder(Color base) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        g.save();
        g.translate(1, 1 + CAP_H / 2);           // 1px 여백 + 타원 절반

        double w = BODY_W;
        double h = BODY_H;

        /* 하단 타원 */
        g.setFill(base.darker());
        g.fillOval(0, h - CAP_H / 2, w, CAP_H);
        g.setStroke(Color.BLACK);
        g.strokeOval(0, h - CAP_H / 2, w, CAP_H);

        /* 몸통 */
        g.setFill(base.darker());
        g.fillRect(0, 0, w, h);
        g.setStroke(Color.BLACK);
        g.strokeLine(0, 0, 0, h);
        g.strokeLine(w, 0, w, h);

        /* 상단 타원 */
        g.setFill(base);
        g.fillOval(0, -CAP_H / 2, w, CAP_H);
        g.setStroke(Color.BLACK);
        g.strokeOval(0, -CAP_H / 2, w, CAP_H);

        /* ---- 텍스트 중앙 배치 ---- */
        g.setFont(TEXT_FONT);
        g.setFill(Color.BLACK);

        Text t = new Text(label);
        t.setFont(TEXT_FONT);
        double txtW = t.getLayoutBounds().getWidth();
        double txtH = t.getLayoutBounds().getHeight();

        double txtX = (w - txtW) / 2;
        double txtY = (h + txtH * 2) / 2;            // optically centered
        g.fillText(label, txtX, txtY);

        g.restore();
    }
}
