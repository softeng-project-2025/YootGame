package view.swing;

import model.dto.GameStateDto;
import model.dto.MessageType;
import model.piece.Piece;
import model.player.Player;
import model.position.Position;
import model.yut.YutResult;
import view.View;
import controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Swing 기반 View: GameStateDto를 받아 화면을 렌더링합니다.
 */
public class SwingView extends JFrame implements View {
    private static final int WINDOW_WIDTH       = 1200;
    private static final int WINDOW_HEIGHT      = 1000;
    private static final int PIECE_OFFSET       = 13;   // 말 겹침 오프셋
    private static final int DEFAULT_YUT_INDEX  = 1;    // 콤보박스 '도' 기본 선택
    private static final int MIN_PLAYERS        = 2;
    private static final int MAX_PLAYERS        = 4;
    private static final int MIN_PIECES         = 2;
    private static final int MAX_PIECES         = 5;
    private static final String[] BOARD_TYPES   = {"square", "pentagon", "hexagon"};

    private GameStateDto currentDto;
    private GameController controller;

    private JLabel resultLabel;
    private JPanel boardPanel;

    private JButton randomThrowButton;
    private JButton selectThrowButton;
    private JButton DoButton, GaeButton, GeolButton, YutButton, MoButton, BackDoButton;
    private JComboBox<String> yutChoiceBox;
    private JLabel statusLabel;

    public SwingView() {
        super("YootGame");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    @Override
    public void setController(GameController ctrl) {
        this.controller = ctrl;
        initUI();
        setVisible(true);
    }

    private void initUI() {
        initResultLabel();
        initControlPanel();
        // boardPanel은 renderGame 호출 시 초기화됩니다
    }

    private void initResultLabel() {
        resultLabel = new JLabel("결과: ", SwingConstants.CENTER);
        add(resultLabel, BorderLayout.NORTH);
    }

    private void initControlPanel() {
        // 버튼 및 콤보박스 생성
        randomThrowButton = new JButton("랜덤 윷 던지기");
        randomThrowButton.addActionListener(e -> controller.onRandomThrow());

        yutChoiceBox = new JComboBox<>(YutResult.getNames());
        yutChoiceBox.setSelectedIndex(DEFAULT_YUT_INDEX); // 기본 '도'

        selectThrowButton = new JButton("지정 윷 던지기");
        selectThrowButton.addActionListener(e -> controller.onDesignatedThrow(
                YutResult.fromName((String) yutChoiceBox.getSelectedItem())
        ));

        // Pending Yut 선택 버튼
        DoButton = new JButton("도 x 0");
        DoButton.addActionListener(e -> controller.onSelectPendingYut(YutResult.DO));

        GaeButton = new JButton("개 x 0");
        GaeButton.addActionListener(e -> controller.onSelectPendingYut(YutResult.GAE));

        GeolButton = new JButton("걸 x 0");
        GeolButton.addActionListener(e -> controller.onSelectPendingYut(YutResult.GEOL));

        YutButton = new JButton("윷 x 0");
        YutButton.addActionListener(e -> controller.onSelectPendingYut(YutResult.YUT));

        MoButton = new JButton("모 x 0");
        MoButton.addActionListener(e -> controller.onSelectPendingYut(YutResult.MO));

        BackDoButton = new JButton("빽도 x 0");
        BackDoButton.addActionListener(e -> controller.onSelectPendingYut(YutResult.BACK_DO));

        // 레이아웃 구성
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtons.add(randomThrowButton);
        leftButtons.add(selectThrowButton);
        leftButtons.add(yutChoiceBox);

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

        // 상태바
        statusLabel = new JLabel("게임을 시작하세요.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    public void renderGame(Object dtoObj) {
        this.currentDto = (GameStateDto) dtoObj;
        GameStateDto dto = (GameStateDto) dtoObj;

        ensureBoardPanel();
        boardPanel.removeAll();

        renderPieces(dto);
        updateControls(dto);

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private void ensureBoardPanel() {
        if (boardPanel == null) {
            boardPanel = new DrawBoard(controller.getCurrentBoardStrategy());
            boardPanel.setLayout(null);
            boardPanel.setBackground(Color.WHITE);
            JScrollPane scrollPane = new JScrollPane(boardPanel);
            add(scrollPane, BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }

    private void renderPieces(GameStateDto dto) {
        Map<GameStateDto.PositionKey, List<GameStateDto.PieceInfo>> groups
                = dto.groupByPosition();

        for (var entry : groups.entrySet()) {
            List<GameStateDto.PieceInfo> infos = entry.getValue();
            int size = infos.size();

            // ② 같은 좌표에 있는 말들끼리 오프셋 처리
            for (int i = 0; i < size; i++) {
                GameStateDto.PieceInfo info = infos.get(i);

                int shift = (int) Math.round(i - (size - 1) / 2.0);
                Position pos = new Position(info.id(), info.x(), info.y() - shift * PIECE_OFFSET);

                CylinderButton btn = new CylinderButton(
                        getPlayerColor(info.ownerId()), pos, "P" + info.ownerId()
                );
                btn.setEnabled(info.selectable());
                btn.addActionListener(e -> controller.onSelectPieceById(info.id()));
                boardPanel.add(btn);
            }
        }
    }

    private void updateControls(GameStateDto dto) {
        // 버튼 활성화 / 라벨 업데이트
        boolean over = dto.gameOver();
        randomThrowButton.setEnabled(!over);
        selectThrowButton.setEnabled(!over && !dto.pendingYuts().isEmpty());
        resultLabel.setText("결과: " + Optional.ofNullable(dto.lastYut()).map(YutResult::getName).orElse(""));
        updateStatus(dto.messageText(), dto.messageType());
        updateMoveButtons(dto.pendingYuts());
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


        boolean finished = currentDto.gameOver();

        randomThrowButton.setEnabled(!finished);
        selectThrowButton.setEnabled(!finished);

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

        SpinnerNumberModel playerModel = new SpinnerNumberModel(MIN_PLAYERS, MIN_PLAYERS, MAX_PLAYERS, 1);
        SpinnerNumberModel pieceModel  = new SpinnerNumberModel(MIN_PIECES, MIN_PIECES, MAX_PIECES, 1);

        JSpinner playerCountSpinner = new JSpinner(playerModel);
        JSpinner pieceCountSpinner  = new JSpinner(pieceModel);
        JComboBox<String> boardTypeBox = new JComboBox<>(BOARD_TYPES);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("플레이어 수:"));
        panel.add(playerCountSpinner);
        panel.add(new JLabel("말 개수:"));
        panel.add(pieceCountSpinner);
        panel.add(new JLabel("보드 타입:"));
        panel.add(boardTypeBox);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "게임 설정",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            // 범위를 벗어날 일이 없으므로 바로 컨트롤러 호출
            int playerCount = (Integer) playerCountSpinner.getValue();
            int pieceCount  = (Integer) pieceCountSpinner.getValue();
            String boardType = (String) boardTypeBox.getSelectedItem();
            controller.initializeGame(playerCount, pieceCount, boardType);
        } else {
            System.exit(0);
        }
    }

    @Override
    public void showWinner(Player winner) {
        String message = winner.getName() + "님이 모두 도착해 승리했습니다!";
        String[] options = {"다시 시작", "종료"};
        int choice = JOptionPane.showOptionDialog(
                this,
                message,
                "게임 종료",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            controller.onRestartGame();
        } else {
            System.exit(0);
        }
    }

    // 새 게임 설정 다이얼로그 전용으로 뷰를 깨끗이 초기화
    private void prepareForNewGameSetup() {

    }

    @Override
    public void promptRestart(GameController controller) {
        int sel = JOptionPane.showConfirmDialog(
                this, "게임을 다시 시작할까요?", "게임 종료",
                JOptionPane.YES_NO_OPTION);
        if (sel == JOptionPane.YES_OPTION) {
            controller.onRestartGame();
        } else {
            System.exit(0);
        }
    }

    @Override
    public void resetUI() {
        // 1) 결과/상태 라벨 초기화
        resultLabel.setText("결과: ");
        statusLabel.setText("게임을 시작하세요.");
        statusLabel.setForeground(Color.BLACK);

        // 2) 윷 던지기/선택 버튼 상태 초기화
        randomThrowButton.setEnabled(true);
        selectThrowButton.setEnabled(true);

        // 3) pending 윷 버튼 카운터 초기화
        updateMoveButtons(List.of());

        // 4) 보드판 위의 말들 제거
        if (boardPanel != null) {
            boardPanel.removeAll();
            boardPanel.revalidate();
            boardPanel.repaint();
        }

        // 기존에 그려진 보드, 버튼 모두 제거
        getContentPane().removeAll();
        boardPanel = null;
        // 레이아웃·컴포넌트 재설정
        setLayout(new BorderLayout());
        initUI();
        revalidate();
        repaint();
    }

    private Color getPlayerColor(int pieceId) {
        Color[] colors = {Color.CYAN, Color.PINK, Color.ORANGE, Color.MAGENTA};
        return colors[(pieceId - 1) % colors.length];
    }

    public void updateMoveButtons(List<YutResult> pending) {
        // 윷 결과별 개수를 세기 위한 EnumMap
        EnumMap<YutResult, Integer> counter = new EnumMap<>(YutResult.class);
        for (YutResult r : YutResult.values()) counter.put(r, 0);   // 0 으로 초기화
        for (YutResult r : pending) {
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
