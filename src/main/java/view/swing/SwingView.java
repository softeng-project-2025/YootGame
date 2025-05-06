package view.swing;

import model.dto.MessageType;
import model.player.Player;
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

    private final JFrame frame;
    private GameController controller;
    private JLabel resultLabel;
    private JPanel boardPanel;
    private JButton throwButton;
    private JButton restartButton;
    private JComboBox<String> yutChoiceBox;
    private JLabel statusLabel;

    /** 1) 생성자에서 initUI() 호출을 제거 */
    public SwingView() {
        frame = new JFrame("YootGame");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 👇 컨트롤러가 아직 없으므로 UI 초기화는 보류
        // initUI();  ❌
        // frame.setVisible(true); ❌  → UI 초기화 후에 호출
    }

    public void showGameSetupDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2));

        JTextField playerCountField = new JTextField("2");
        JTextField pieceCountField = new JTextField("3");
        JComboBox<String> boardTypeBox = new JComboBox<>(new String[]{"square", "pentagon", "hexagon"});

        panel.add(new JLabel("플레이어 수:"));
        panel.add(playerCountField);
        panel.add(new JLabel("말 개수:"));
        panel.add(pieceCountField);
        panel.add(new JLabel("보드 타입:"));
        panel.add(boardTypeBox);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "게임 설정", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int playerCount = Integer.parseInt(playerCountField.getText());
                int pieceCount = Integer.parseInt(pieceCountField.getText());
                String boardType = (String) boardTypeBox.getSelectedItem();

                controller.initializeGame(playerCount, pieceCount, boardType);

            } catch (NumberFormatException e) {
                showMessage("숫자를 올바르게 입력해주세요.");
                showGameSetupDialog(); // 재귀 호출로 다시 띄움
            }
        } else {
            System.exit(0); // 취소 누르면 종료
        }
    }

    public void updateStatus(String message) {
        statusLabel.setText(message);
    }

    /** 3) initUI() : 보드 생성 부분 제거 */
    private void initUI() {
        // 게임 보드는 아직 없으므로 boardPanel 생성 ❌
        resultLabel = new JLabel("결과: ");
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(resultLabel, BorderLayout.NORTH);

        throwButton = new JButton("윷 던지기");
        throwButton.addActionListener(e -> {
            String choice = (String) yutChoiceBox.getSelectedItem();
            YutResult result = "랜덤".equals(choice)
                           ? YutThrower.throwYut()
                           : YutResult.fromName(choice);
            controller.handleYutThrow(result);
        });

        restartButton = new JButton("다시 시작");
        restartButton.setEnabled(false);
        restartButton.addActionListener(e ->
        controller.initializeGame(2, 3, "square")
    );

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(throwButton);
        buttonPanel.add(restartButton);
        yutChoiceBox = new JComboBox<>(new String[]{"랜덤", "도", "개", "걸", "윷", "모", "빽도"});
        buttonPanel.add(yutChoiceBox);

        statusLabel = new JLabel("게임을 시작하세요.");
        statusLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        frame.add(bottomPanel, BorderLayout.SOUTH);
    }

    /** 2) 컨트롤러 주입 메서드에서 UI를 초기화 */
    public void setController(GameController controller) {
        this.controller = controller;
        initUI();           // 이제 controller가 null이 아님
        frame.setVisible(true); // UI가 완성된 뒤에 화면 표시
    }

    /** renderGame() : 최초 호출 시 보드 생성 & 중앙에 배치 */
    public void renderGame(Game game) {
        if (boardPanel == null) {                             // ★ 보드가 아직 없을 때만 생성
            boardPanel = new DrawBoard(game.getBoard().getPathStrategy());
            boardPanel.setLayout(null);
            boardPanel.setPreferredSize(new Dimension(800, 800));
            boardPanel.setBackground(Color.WHITE);
            JScrollPane scrollPane = new JScrollPane(boardPanel);
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.validate();                                 // 레이아웃 갱신
        }

        boardPanel.removeAll();

        // --- 이후 말 그리기 로직은 그대로 ---
        Map<Integer, List<Piece>> positionMap = new HashMap<>();
        for (var player : game.getPlayers()) {
            for (var piece : player.getPieces()) {
                int idx = piece.getPosition().getIndex();
                positionMap.computeIfAbsent(idx, k -> new ArrayList<>()).add(piece);
            }
        }

        for (List<Piece> piecesAtPosition : positionMap.values()) {
            if (piecesAtPosition.isEmpty()) continue;
            Position pos = piecesAtPosition.get(0).getPosition();
            int baseX = pos.getX();
            int baseY = pos.getY();

            for (int i = 0; i < piecesAtPosition.size(); i++) {
                Piece piece = piecesAtPosition.get(i);
                int playerNum = piece.getOwner().getPlayerNumber();
                String label = "P" + playerNum + "-" + piece.getId();
                JButton pieceButton = new JButton(label);

                Color[] playerColors = {Color.CYAN, Color.PINK, Color.ORANGE, Color.MAGENTA};
                pieceButton.setBackground(playerColors[(playerNum - 1) % playerColors.length]);
                pieceButton.setOpaque(true);
                pieceButton.setBorderPainted(false);
                pieceButton.setFont(new Font("Arial", Font.PLAIN, 1));
                pieceButton.setMargin(new Insets(0, 0, 0, 0));
                pieceButton.setToolTipText(piece.getOwner().getName() + "의 말 " + piece.getId());

                int offsetX = baseX + i * 6;
                int offsetY = baseY + i * 6;
                pieceButton.setBounds(offsetX, offsetY, 60, 60);
                pieceButton.addActionListener(e -> controller.handlePieceSelect(piece));

                boardPanel.add(pieceButton);
            }
        }

        boolean finished = game.isFinished();
        throwButton.setEnabled(!finished);
        restartButton.setEnabled(finished);

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void updateYutResult(YutResult result) {
        resultLabel.setText("결과: " + result.name());
        resultLabel.setForeground(Color.BLUE); // 시각적으로 더 강조
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
            resetUI();
        } else {
            throwButton.setEnabled(false);
            showMessage("게임을 종료합니다.");
            System.exit(0); // 게임 종료
        }
    }

    @Override
    public void showWinner(Player winner) {
        JOptionPane.showMessageDialog(null,
                winner.getName() + "님이 모든 말을 도착시켜 승리했습니다!",
                "게임 종료",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void updateStatus(String message, MessageType type) {
        Color color = switch (type) {
            case INFO -> new Color(33, 150, 243);     // 파랑
            case WARN -> new Color(255, 152, 0);      // 주황
            case ERROR -> new Color(244, 67, 54);     // 빨강
        };
        String icon = switch (type) {
            case INFO -> "";
            case WARN -> "⚠️";
            case ERROR -> "";
        };

        statusLabel.setForeground(color); // JLabel 등 UI 컴포넌트 색 변경
        statusLabel.setText(icon + message);
    }

    public void resetUI() {
        updateStatus("게임을 시작하세요.");
        resultLabel.setText("결과: ");
        restartButton.setEnabled(false);
        throwButton.setEnabled(true);
    }
}