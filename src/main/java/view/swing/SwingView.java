package view.swing;

import model.dto.GameStateDto;
import model.dto.MessageType;
import model.dto.Phase;
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
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final EnumMap<YutResult, JButton> pendingButtons = new EnumMap<>(YutResult.class);
    private JComboBox<String> yutChoiceBox;
    private JLabel statusLabel;
    private JLabel currentPlayerLabel;

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
        resultLabel = new JLabel("결과: ", SwingConstants.LEFT);

        currentPlayerLabel = new JLabel("현재 차례: –", SwingConstants.RIGHT);
        currentPlayerLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));

        JPanel header = new JPanel(new BorderLayout());
        header.add(resultLabel, BorderLayout.WEST);
        header.add(currentPlayerLabel, BorderLayout.EAST);
        header.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        add(header, BorderLayout.NORTH);

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

        // pending 선택 버튼
        JButton doBtn, gaeBtn, geolBtn, yutBtn, moBtn, backDoBtn;
        doBtn   = makePendingButton("도",   YutResult.DO);
        gaeBtn  = makePendingButton("개",  YutResult.GAE);
        geolBtn = makePendingButton("걸", YutResult.GEOL);
        yutBtn  = makePendingButton("윷",  YutResult.YUT);
        moBtn   = makePendingButton("모",   YutResult.MO);
        backDoBtn = makePendingButton("빽도", YutResult.BACK_DO);

        pendingButtons.put(YutResult.DO,        doBtn);
        pendingButtons.put(YutResult.GAE,       gaeBtn);
        pendingButtons.put(YutResult.GEOL,      geolBtn);
        pendingButtons.put(YutResult.YUT,       yutBtn);
        pendingButtons.put(YutResult.MO,        moBtn);
        pendingButtons.put(YutResult.BACK_DO,   backDoBtn);

        // 레이아웃 구성
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(randomThrowButton);
        left.add(selectThrowButton);
        left.add(yutChoiceBox);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        for (JButton b : List.of(doBtn, gaeBtn, geolBtn, yutBtn, moBtn, backDoBtn))
            right.add(b);

        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(left, BorderLayout.WEST);
        controlPanel.add(right, BorderLayout.EAST);

        // 상태바
        statusLabel = new JLabel("게임을 시작하세요.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(controlPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton makePendingButton(String label, YutResult r) {
        JButton b = new JButton(label + " x 0");
        b.addActionListener(e -> controller.onSelectPendingYut(r));
        return b;
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
            add(new JScrollPane(boardPanel), BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }

    private void renderPieces(GameStateDto dto) {
        boolean canSelect = dto.phase() == Phase.CAN_SELECT;
        currentDto.groupByPosition().forEach((posKey, list) -> {
            int size = list.size();
            for (int i=0; i<size; i++) {
                var info = list.get(i);
                int shift = (int)Math.round(i - (size-1)/2.0);
                Position p = new Position(info.id(), info.x(), info.y() - shift*PIECE_OFFSET);

                CylinderButton btn = new CylinderButton(
                        getPlayerColor(info.ownerId()), p, "P"+info.ownerId()
                );
                btn.setEnabled(canSelect && info.selectable());
                btn.addActionListener(e -> controller.onSelectPieceById(info.id()));
                boardPanel.add(btn);
            }
        });
    }

    private void updateControls(GameStateDto dto) {
        Phase phase = dto.phase();

        // 던지기 모드 (Phase.CAN_THROW) 에만 활성화
        boolean canThrow = phase == Phase.CAN_THROW;
        randomThrowButton.setEnabled(canThrow);
        selectThrowButton.setEnabled(canThrow);
        yutChoiceBox.setEnabled(canThrow);

        // 선택 모드 (Phase.CAN_SELECT) 에만 pending 버튼 활성화
        boolean canSelect = dto.phase() == Phase.CAN_SELECT;
        List<YutResult> pend = currentDto.pendingYuts();
        for (YutResult r : YutResult.values()) {
            int cnt = (int)pend.stream().filter(x->x==r).count();
            JButton b = pendingButtons.get(r);
            b.setText(r.getName()+" x "+cnt);
            b.setEnabled(canSelect && cnt>0);
        }

        // 5) 기타 UI 업데이트
        resultLabel.setText("결과: " + currentDto.findLastYut());
        currentPlayerLabel.setText("현재 차례: " + currentDto.findCurrentPlayer());
        updateStatus(currentDto.messageText(), currentDto.messageType());
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


        boardPanel.revalidate();
        boardPanel.repaint();
        statusLabel.setForeground(color);
        statusLabel.setText(message);
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
        int choice = JOptionPane.showOptionDialog(
                this, winner.getName()+"님 승리!", "게임 종료",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, new String[]{"다시 시작","종료"}, "다시 시작"
        );

        if (choice==0) controller.onRestartGame();
        else System.exit(0);
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

        // 카운터 계산
        Map<YutResult, Long> counts = pending.stream()
                .collect(Collectors.groupingBy(Function.identity(),
                        () -> new EnumMap<>(YutResult.class),
                        Collectors.counting()));

        // 한 번의 루프로 버튼 업데이트
        for (YutResult r : YutResult.values()) {
            int cnt = counts.getOrDefault(r, 0L).intValue();
            JButton btn = pendingButtons.get(r);
            btn.setText(r.getName() + " x " + cnt);
            btn.setEnabled(cnt > 0 && !currentDto.gameOver());
        }
    }
}
