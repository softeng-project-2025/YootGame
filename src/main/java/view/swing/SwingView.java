package view.swing;

import model.yut.YutThrower;
import view.View;
import controller.GameController;
import model.Game;
import model.piece.Piece;
import model.position.Position;
import model.yut.YutResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class SwingView extends JFrame implements View {

    private GameController controller;
    private JLabel resultLabel;
    private JPanel boardPanel;
    private JButton throwButton;
    private JButton restartButton;
    private JComboBox<String> yutChoiceBox;


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

        resultLabel = new JLabel("결과: ");
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(resultLabel, BorderLayout.NORTH);

        throwButton = new JButton("윷 던지기");
        throwButton.addActionListener(e -> {
            String choice = (String) yutChoiceBox.getSelectedItem();
            YutResult result;

            if ("랜덤".equals(choice)) {
                result = YutThrower.throwYut(); // 기존 랜덤 윷 던지기 로직
            } else {
                result = YutResult.fromName(choice); // 한글 → enum 매핑
            }

            controller.handleYutThrow(result);
        });
        restartButton = new JButton("다시 시작");
        restartButton.setEnabled(false); // 초기엔 비활성화
        restartButton.addActionListener(e -> {
            controller.initializeGame(2, 3, "square"); // 기본값 또는 사용자 입력값
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(throwButton);
        buttonPanel.add(restartButton);
        yutChoiceBox = new JComboBox<>(new String[]{"랜덤", "도", "개", "걸", "윷", "모", "빽도"});
        buttonPanel.add(yutChoiceBox);

        add(boardPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Controller에서 주입
    public void setController(GameController controller) {
        this.controller = controller;
    }

    // 게임 상태 기반으로 화면을 갱신 (말 위치 등)
    public void renderGame(Game game) {
        boardPanel.removeAll();

        // 위치별 말 리스트를 위한 맵: Position index -> 말들
        Map<Integer, List<Piece>> positionMap = new HashMap<>();

        for (var player : game.getPlayers()) {
            for (var piece : player.getPieces()) {
                int idx = piece.getPosition().getIndex();
                positionMap.computeIfAbsent(idx, k -> new ArrayList<>()).add(piece);
            }
        }

        // 위치별로 버튼 배치 (오프셋 적용)
        for (List<Piece> piecesAtPosition : positionMap.values()) {
            if (piecesAtPosition.isEmpty()) continue;

            Position pos = piecesAtPosition.get(0).getPosition();
            int baseX = pos.getX();
            int baseY = pos.getY();

            for (int i = 0; i < piecesAtPosition.size(); i++) {
                Piece piece = piecesAtPosition.get(i);

                JButton pieceButton = new JButton(piece.getOwner().getName().charAt(piece.getOwner().getName().length() - 1) + "-" + piece.getId());

                // 오프셋 위치 (ex: 6픽셀씩 우하향 이동)
                int offsetX = baseX + i * 6;
                int offsetY = baseY + i * 6;

                pieceButton.setBounds(offsetX, offsetY, 40, 40);
                pieceButton.addActionListener(e -> controller.handlePieceSelect(piece));

                boardPanel.add(pieceButton);
            }
        }
        if (game.isFinished()) {
            throwButton.setEnabled(false);
        } else {
            throwButton.setEnabled(true);
        }
        boolean finished = game.isFinished();
        throwButton.setEnabled(!finished);
        restartButton.setEnabled(finished); // 게임 끝났을 때만 가능

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
    public void updateYutResult(YutResult result) {
        resultLabel.setText("결과: " + result.name());
    }

    public void promptRestart(GameController controller) {
        int result = JOptionPane.showConfirmDialog(
                this,
                "게임을 다시 시작할까요?",
                "게임 종료",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            controller.initializeGame(2, 3, "square"); // 예시 값
        } else {
            throwButton.setEnabled(false);
            showMessage("게임을 종료합니다.");
        }
    }
}


