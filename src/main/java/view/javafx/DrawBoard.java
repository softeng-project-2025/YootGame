package view.javafx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import model.position.Position;
import model.strategy.PathStrategy;

import java.util.List;

/**
 * DrawBoard – prefSize 고정 버전
 *  · ScrollPane 초기 표시 문제 해결
 *  · Swing 과 동일한 선 굵기 1 px
 */
public class DrawBoard extends Pane {

    private static final int BOARD_W  = 600;   // 실제 보드 한 변
    private static final int MARGIN_X = 50;    // 좌우 여백
    private static final int PREF_W   = 1200;
    private static final int PREF_H   = 800;              // 세로는 보드만

    private final PathStrategy pathStrategy;
    private final Canvas       canvas = new Canvas(PREF_W, PREF_H);

    public DrawBoard(PathStrategy pathStrategy) {
        this.pathStrategy = pathStrategy;
        getChildren().add(canvas);

        /* 폭·높이 변할 때마다 다시 그리기 */
        widthProperty().addListener((o, ov, nv) -> draw());
        heightProperty().addListener((o, ov, nv) -> draw());

        draw();   // 이제는 처음에도 폭·높이가 0 이 아님
    }

    /* Pane 이 부모에게 보고할 선호 크기 */
    @Override protected double computePrefWidth(double h)  { return PREF_W; }
    @Override protected double computePrefHeight(double w) { return PREF_H; }

    @Override protected void layoutChildren() {
        super.layoutChildren();
        draw();                     // 위치·클립 갱신 뒤에도 항상 그리기
    }

    /*------------------- 실제 그리기 -------------------*/
    private void draw() {
        // 캔버스는 고정 크기이므로 굳이 setWidth/Height() 없이도 됨
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, PREF_W, PREF_H);
        g.setStroke(Color.BLACK);
        g.setLineWidth(1);

        List<Position> allPos    = pathStrategy.getAllPositions();
        List<Position> vertexPos = pathStrategy.getAllVertexPositions();

        double marginY = (PREF_H - BOARD_W) / 2.0; // 여기선 0

        /* 1) 선분 */
        for (int i = 0; i < vertexPos.size(); i += 2) {
            Position p1 = vertexPos.get(i), p2 = vertexPos.get(i + 1);
            g.strokeLine(p1.x() + MARGIN_X, p1.y() + marginY,
                    p2.x() + MARGIN_X, p2.y() + marginY);
        }

        /* 2) 노은(원) */
        for (Position p : allPos) {
            if (p.isCenter() || p.isDiagonalEntry()) drawNoon(p, g, 30, marginY);
            drawNoon(p, g, 20, marginY);
        }
    }

    private void drawNoon(Position p, GraphicsContext g, int r, double marginY) {
        double x = p.x() + MARGIN_X - r;
        double y = p.y() + marginY - r;

        g.setFill(Color.WHITE);
        g.fillOval(x, y, r * 2, r * 2);
        g.setStroke(Color.BLACK);
        g.strokeOval(x, y, r * 2, r * 2);
    }
}
