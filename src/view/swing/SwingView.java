package view.swing;

import controller.GameController;
import model.Game;
import model.piece.Piece;
import model.yut.YutResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingView extends JFrame {

    private GameController controller;
    private JPanel boardPanel;
    private JButton throwButton;

    public SwingView() {
        setTitle("YootGame");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();
        setVisible(true);
    }

    private void initUI() {
        boardPanel = new JPanel();
        boardPanel.setLayout(null);
        boardPanel.setPreferredSize(new Dimension(600, 600));
        boardPanel.setBackground(Color.WHITE);

        throwButton = new JButton("윷 던지기");
        throwButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 테스트용으로 도 고정
                controller.handleYutThrow(YutResult.DO);
            }
        });

        add(boardPanel, BorderLayout.CENTER);
        add(throwButton, BorderLayout.SOUTH);
    }

    // Controller에서 주입
    public void setController(GameController controller) {
        this.controller = controller;
    }

    // 게임 상태 기반으로 화면을 갱신 (말 위치 등)
    public void renderGame(Game game) {
        boardPanel.removeAll();

        for (var player : game.getPlayers()) {
            for (var piece : player.getPieces()) {
                JButton pieceButton = new JButton(player.getName().charAt(player.getName().length() - 1) + "-" + piece.getId());

                // 말의 좌표 위치 가져오기
                int x = piece.getPosition().getX();
                int y = piece.getPosition().getY();

                // 좌표 기반으로 버튼 위치 지정 (폭, 높이는 임의로 40)
                pieceButton.setBounds(x, y, 40, 40);

                // 클릭 시 controller에 알림
                pieceButton.addActionListener(e -> controller.handlePieceSelect(piece));

                boardPanel.add(pieceButton);
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }
}
