package view.swing;

import model.dto.GameStateDto;
import model.dto.MessageType;
import model.dto.GameStateDto.PieceInfo;
import model.dto.GameStateDto.PlayerInfo;
import model.piece.Piece;
import model.player.Player;
import model.position.Position;
import model.strategy.PathStrategy;
import model.yut.YutResult;
import view.View;
import controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.EnumMap;

/**
 * Swing 기반 View: GameStateDto를 받아 화면을 렌더링합니다.
 */
public class SwingView extends JFrame implements View {

    private GameController controller;
    private JLabel resultLabel;
    private JPanel boardPanel;
    private JButton randomThrowButton;
    private JButton selectThrowButton;
    private JButton restartButton;
    private JButton DoButton;
    private JButton GaeButton;
    private JButton GeolButton;
    private JButton YutButton;
    private JButton MoButton;
    private JButton BackDoButton;
    private JComboBox<String> yutChoiceBox;
    private JLabel statusLabel;

    public SwingView() {
        super("YootGame");
        setSize(1200, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    @Override
    public void setController(GameController ctrl) {
        this.controller = ctrl;
        initUI();
        setVisible(true);
    }

    @Override
    public void renderGame(Object dtoObj) {
        GameStateDto dto = (GameStateDto) dtoObj;
        if (boardPanel == null) {
            boardPanel = new DrawBoard(controller.getCurrentBoardStrategy());
            boardPanel.setLayout(null);
            boardPanel.setBackground(Color.WHITE);
            JScrollPane scrollPane = new JScrollPane(boardPanel);
            add(scrollPane, BorderLayout.CENTER);
        }
        boardPanel.removeAll();

        // PieceInfo 리스트로 버튼 생성
        for (PieceInfo info : dto.pieces()) {
            CylinderButton btn = new CylinderButton(
                    getPlayerColor(info.id()),
                    new Position(info.id(),info.x(), info.y()),
                    "P" + info.id()
            );
            btn.setEnabled(info.selectable());
            btn.addActionListener(e -> controller.onSelectPieceById(info.id()));
            boardPanel.add(btn);
        }

        boolean gameOver = dto.gameOver();
        randomThrowButton.setEnabled(!gameOver);
        selectThrowButton.setEnabled(!gameOver && dto.pieces().stream().anyMatch(PieceInfo::selectable));
        restartButton.setEnabled(gameOver);

        // 결과 및 상태 표시
        resultLabel.setText("결과: " + (dto.yutResult() != null ? dto.yutResult().getName() : ""));
        updateStatus(dto.messageText(), dto.messageType());

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private void initUI() {
        resultLabel = new JLabel("결과: ", SwingConstants.CENTER);
        add(resultLabel, BorderLayout.NORTH);

        randomThrowButton = new JButton("랜덤 윷 던지기");
        randomThrowButton.addActionListener(e -> controller.onRandomThrow());

        selectThrowButton = new JButton("지정 윷 던지기");
        selectThrowButton.addActionListener(e -> controller.onDesignatedThrow(
                YutResult.fromName((String) yutChoiceBox.getSelectedItem())
        ));

        restartButton = new JButton("다시 시작");
        restartButton.setEnabled(false);
        restartButton.addActionListener(e -> controller.initializeGameDialog());

        yutChoiceBox = new JComboBox<>(new String[]{"도", "개", "걸", "윷", "모", "빽도"});

        selectThrowButton = new JButton("지정 윷 던지기");
        selectThrowButton.addActionListener(e -> {
            String choice = (String) yutChoiceBox.getSelectedItem();
            YutResult result = YutResult.fromName(choice);
            controller.throwYut();
        });
        yutChoiceBox = new JComboBox<>(YutResult.getNames());

        DoButton = new JButton("도 x 0");
        GaeButton = new JButton("개 x 0");
        GeolButton = new JButton("걸 x 0");
        YutButton = new JButton("윷 x 0");
        MoButton = new JButton("모 x 0");
        BackDoButton = new JButton("빽도 x 0");

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtons.add(randomThrowButton);
        leftButtons.add(restartButton);
        leftButtons.add(selectThrowButton);
        leftButtons.add(yutChoiceBox);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(randomThrowButton);
        buttonPanel.add(selectThrowButton);
        buttonPanel.add(yutChoiceBox);
        buttonPanel.add(restartButton);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtons.add(DoButton);
        rightButtons.add(GaeButton);
        rightButtons.add(GeolButton);
        rightButtons.add(YutButton);
        rightButtons.add(MoButton);
        rightButtons.add(BackDoButton);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(leftButtons, BorderLayout.WEST);
        buttonPanel.add(rightButtons, BorderLayout.EAST);

        statusLabel = new JLabel("게임을 시작하세요.");
        statusLabel = new JLabel("게임을 시작하세요.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }


    @Override
    public void updateStatus(String message, MessageType type) {
        Color color;
        switch (type) {
            case INFO -> color = new Color(33, 150, 243);
            case WARN -> color = new Color(255, 152, 0);
            case GAME_OVER, ERROR -> color = new Color(244, 67, 54);
            default -> color = Color.BLACK;
        }

        updateMoveButtons(game.getTurnResult());

        boolean finished = game.isFinished();
        randomThrowButton.setEnabled(!finished);
        selectThrowButton.setEnabled(!finished);
        restartButton.setEnabled(finished);

        boardPanel.revalidate();
        boardPanel.repaint();
        statusLabel.setForeground(color);
        statusLabel.setText(message);
    }

    @Override
    public void showSelectablePieces(List<Piece> pieces) {
        // DTO 렌더링으로 대체됨
    }

    @Override
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

    @Override
    public void showWinner(Player winner) {
        JOptionPane.showMessageDialog(this,
                winner.getName() + "님이 모두 도착해 승리했습니다!",
                "게임 종료",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void promptRestart(GameController controller) {
        int sel = JOptionPane.showConfirmDialog(
                this, "게임을 다시 시작할까요?", "게임 종료",
                JOptionPane.YES_NO_OPTION);
        if (sel == JOptionPane.YES_OPTION) controller.initializeGameDialog();
        else System.exit(0);
    }

    private Color getPlayerColor(int pieceId) {
        Color[] colors = {Color.CYAN, Color.PINK, Color.ORANGE, Color.MAGENTA};
        return colors[(pieceId - 1) % colors.length];
    }

    public void resetUI() {
        updateStatus("게임을 시작하세요.");
        resultLabel.setText("결과: ");
        restartButton.setEnabled(false);
        randomThrowButton.setEnabled(true);
        selectThrowButton.setEnabled(true);
    }

    public void updateMoveButtons(TurnResult turnResult) {
        if (turnResult == null) {
            return;
        }

        // 윷 결과별 개수를 세기 위한 EnumMap
        EnumMap<YutResult, Integer> counter = new EnumMap<>(YutResult.class);
        for (YutResult r : YutResult.values()) counter.put(r, 0);   // 0 으로 초기화
        for (YutResult r : turnResult.getAvailable()) {
            counter.merge(r, 1, Integer::sum);
        }

        // 버튼 텍스트 갱신
        DoButton.setText(   "도 x "  + counter.get(YutResult.DO));
        GaeButton.setText(  "개 x "  + counter.get(YutResult.GAE));
        GeolButton.setText( "걸 x "  + counter.get(YutResult.GEOL));
        YutButton.setText(  "윷 x "  + counter.get(YutResult.YUT));
        MoButton.setText(   "모 x "  + counter.get(YutResult.MO));
        BackDoButton.setText("빽도 x " + counter.get(YutResult.BACK_DO));
    }
}
