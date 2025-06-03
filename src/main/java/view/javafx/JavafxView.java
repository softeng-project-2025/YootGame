package view.javafx;

import controller.GameController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.dto.GameStateDto;
import model.dto.MessageType;
import model.dto.Phase;
import model.player.Player;
import model.position.Position;
import model.yut.YutResult;
import view.View;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * SwingView → Java FX Stage 로 교체.
 *   · 메서드 시그니처·상수·컨트롤러 호출은 기존 그대로
 */
public class JavafxView extends Stage implements View {

    /* 창 & 게임 설정 상수 ---------------------------------------------------*/
    private static final int WINDOW_W           = 1200;
    private static final int WINDOW_H           = 1000;
    private static final int PIECE_OFFSET       = 13;
    private static final int DEFAULT_YUT_INDEX  = 1;
    private static final int MIN_PLAYERS        = 2, MAX_PLAYERS = 4;
    private static final int MIN_PIECES         = 2, MAX_PIECES  = 5;
    private static final String[] BOARD_TYPES   = {"square", "pentagon", "hexagon"};

    /* MVC 연결 */
    private GameController controller;
    private GameStateDto   currentDto;

    /* UI 컴포넌트 -----------------------------------------------------------*/
    private Label  resultLbl, currentPlayerLbl, statusLbl;
    private DrawBoard boardPane;                  // 스크롤 안에 들어감

    private Button randomThrowBtn, selectThrowBtn;
    private ComboBox<String> yutChoiceBox;
    private final Map<YutResult, Button> pendingBtns = new EnumMap<>(YutResult.class);

    /* ----------------------------------------------------------------------*/
    public JavafxView() { Platform.runLater(this::initStage); }

    /* 초기 레이아웃 --------------------------------------------------------*/
    private void initStage() {
        setTitle("Yut Game – Java FX");
        BorderPane root = new BorderPane();
        setScene(new Scene(root, WINDOW_W, WINDOW_H));

        /* (1) 상단 헤더 ---------------------------------------------------*/
        resultLbl        = new Label("결과: ");
        currentPlayerLbl = new Label("현재 차례: –");
        currentPlayerLbl.setStyle("-fx-font-weight:bold; -fx-font-size:14;");

        HBox header = new HBox(resultLbl, new Region(), currentPlayerLbl);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setPadding(new Insets(5,10,5,10));
        root.setTop(header);

        /* (2) 하단 컨트롤 + 상태바 ----------------------------------------*/
        initControlPanel(root);

        /* X 버튼 클릭 = 종료 */
        setOnCloseRequest(e -> Platform.exit());
    }

    private void initControlPanel(BorderPane root) {
        /* – 윷 던지기 관련 ------------------------------------------------*/
        randomThrowBtn = new Button("랜덤 윷 던지기");
        randomThrowBtn.setOnAction(e -> controller.onRandomThrow());

        yutChoiceBox = new ComboBox<>();
        yutChoiceBox.getItems().addAll(YutResult.getNames());
        yutChoiceBox.getSelectionModel().select(DEFAULT_YUT_INDEX);

        selectThrowBtn = new Button("지정 윷 던지기");
        selectThrowBtn.setOnAction(e -> {
            int idx = yutChoiceBox.getSelectionModel().getSelectedIndex();
            controller.onDesignatedThrow(YutResult.values()[idx]);
        });

        HBox left = new HBox(10, randomThrowBtn, selectThrowBtn, yutChoiceBox);
        left.setAlignment(Pos.CENTER_LEFT);

        /* – pending 윷 버튼 ---------------------------------------------*/
        Button doBtn    = makePendingBtn("도",   YutResult.DO);
        Button gaeBtn   = makePendingBtn("개",   YutResult.GAE);
        Button geolBtn  = makePendingBtn("걸",   YutResult.GEOL);
        Button yutBtn   = makePendingBtn("윷",   YutResult.YUT);
        Button moBtn    = makePendingBtn("모",   YutResult.MO);
        Button backDoBtn= makePendingBtn("빽도", YutResult.BACK_DO);

        HBox right = new HBox(6, doBtn, gaeBtn, geolBtn, yutBtn, moBtn, backDoBtn);
        right.setAlignment(Pos.CENTER_RIGHT);

        BorderPane control = new BorderPane(left, null, right, null, null);

        /* – 상태바 -------------------------------------------------------*/
        statusLbl = new Label("게임을 시작하세요.");
        statusLbl.setStyle("-fx-font-weight:bold; -fx-font-size:16;");
        VBox bottom = new VBox(control, statusLbl);
        root.setBottom(bottom);
    }

    private Button makePendingBtn(String txt, YutResult r) {
        Button b = new Button(txt + " x 0");
        b.setDisable(true);
        b.setOnAction(e -> controller.onSelectPendingYut(r));
        pendingBtns.put(r, b);
        return b;
    }

    /*--- View 인터페이스 구현 ---------------------------------------------*/
    @Override public void setController(GameController c) { controller = c; show(); }

    @Override public void renderGame(Object obj) {
        if (!(obj instanceof GameStateDto dto)) return;
        currentDto = dto;

        Platform.runLater(() -> {
            ensureBoardPane();
            boardPane.getChildren().removeIf(n -> n instanceof CylinderButton);

            /* 1) 헤더·결과 */
            resultLbl.setText("결과: " + dto.findLastYut());
            currentPlayerLbl.setText("현재 차례: " + currentDto.findCurrentPlayer());

            /* 2) 말 배치 */
            boolean canSelect = dto.phase() == Phase.CAN_SELECT;
            dto.groupByPosition().forEach((pos, list) -> {
                int n = list.size();
                for (int i = 0; i < n; i++) {
                    var info = list.get(i);
                    int shift = (int) Math.round(i - (n - 1) / 2.0);
                    Position p = new Position(info.id(), info.x(),
                            info.y() - shift * PIECE_OFFSET);

                    CylinderButton cb = new CylinderButton(
                            playerColor(info.ownerId()), p, "P" + info.ownerId());
                    cb.setDisable(!(canSelect && info.selectable()));
                    cb.setOnMouseClicked(e -> controller.onSelectPieceById(info.id()));
                    boardPane.getChildren().add(cb);
                }
            });

            /* 3) pending 윷 & 버튼 활성화 */
            boolean canThrow = dto.phase() == Phase.CAN_THROW;
            randomThrowBtn.setDisable(!canThrow);
            selectThrowBtn.setDisable(!canThrow);
            yutChoiceBox.setDisable(!canThrow);

            updateMoveButtons(dto.pendingYuts());

            resultLbl.setText("결과: " + dto.findLastYut());
            currentPlayerLbl.setText("현재 차례: " + dto.findCurrentPlayer());
            updateStatus(dto.messageText(), dto.messageType());
        });
    }

    @Override public void promptRestart(GameController c) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                    "게임을 다시 시작할까요?", ButtonType.YES, ButtonType.NO);
            a.setHeaderText(null); a.setTitle("게임 종료");
            if (a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) c.onRestartGame();
            else Platform.exit();
        });
    }

    @Override public void showWinner(Player w) {
        Platform.runLater(() -> {
            ButtonType again = new ButtonType("다시 시작", ButtonBar.ButtonData.YES);
            ButtonType quit  = new ButtonType("종료",      ButtonBar.ButtonData.NO);

            Alert a = new Alert(Alert.AlertType.INFORMATION, w.getName() + "님 승리!",
                    again, quit);
            a.setHeaderText(null); a.setTitle("게임 종료");
            if (a.showAndWait().orElse(quit) == again) controller.onRestartGame();
            else Platform.exit();
        });
    }

    @Override public void updateStatus(String msg, MessageType tp) {
        Platform.runLater(() -> {
            Color c = switch (tp) {
                case INFO  -> Color.web("#2196F3");
                case WARN  -> Color.web("#FF9800");
                case GAME_OVER, ERROR -> Color.web("#F44336");
                default    -> Color.BLACK;
            };
            statusLbl.setTextFill(c); statusLbl.setText(msg);
        });
    }

    @Override public void showGameSetupDialog() {
        Platform.runLater(() -> {
            Dialog<ButtonType> d = new Dialog<>();
            d.setTitle("게임 설정");

            Spinner<Integer> spPlayer = new Spinner<>(MIN_PLAYERS, MAX_PLAYERS, MIN_PLAYERS);
            Spinner<Integer> spPiece  = new Spinner<>(MIN_PIECES,  MAX_PIECES,  MIN_PIECES);
            ComboBox<String> cbBoard  = new ComboBox<>();
            cbBoard.getItems().addAll(BOARD_TYPES); cbBoard.getSelectionModel().select(0);

            GridPane g = new GridPane(); g.setHgap(10); g.setVgap(10);
            g.addRow(0, new Label("플레이어 수:"), spPlayer);
            g.addRow(1, new Label("말 개수:"),     spPiece);
            g.addRow(2, new Label("보드 타입:"),   cbBoard);
            d.getDialogPane().setContent(g);
            d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            if (d.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                controller.initializeGame(spPlayer.getValue(), spPiece.getValue(),
                        cbBoard.getValue());
            } else Platform.exit();
        });
    }

    @Override public void showMessage(String m) {
        Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, m,
                ButtonType.OK).showAndWait());
    }

    @Override public void resetUI() {
        Platform.runLater(() -> {
            resultLbl.setText("결과: ");
            statusLbl.setText("게임을 시작하세요.");
            statusLbl.setTextFill(Color.BLACK);

            randomThrowBtn.setDisable(false);
            selectThrowBtn.setDisable(false);
            updateMoveButtons(List.of());

            if (boardPane != null)
                boardPane.getChildren().removeIf(n -> n instanceof CylinderButton);

            BorderPane root = (BorderPane) getScene().getRoot();
            root.setCenter(null);
            boardPane = null;
        });
    }

    /*--- 내부 --------------------------------------------------------------*/
    private void ensureBoardPane() {
        if (boardPane != null) return;

        boardPane = new DrawBoard(controller.getCurrentBoardStrategy());
        boardPane.setStyle("-fx-background-color:white;");
        ((BorderPane) getScene().getRoot())
                .setCenter(new ScrollPane(boardPane));
    }

    private void updateMoveButtons(List<YutResult> pending) {
        Map<YutResult, Long> cnt =
                pending.stream().collect(Collectors.groupingBy(Function.identity(),
                        () -> new EnumMap<>(YutResult.class), Collectors.counting()));

        for (YutResult r : YutResult.values()) {
            int n = cnt.getOrDefault(r, 0L).intValue();
            Button b = pendingBtns.get(r);
            b.setText(r.getName() + " x " + n);
            b.setDisable(n == 0 || currentDto.gameOver());
        }
    }

    private Color playerColor(int id) {
        return switch (id % 4) {
            case 0 -> Color.CORNFLOWERBLUE;
            case 1 -> Color.CRIMSON;
            case 2 -> Color.DARKORANGE;
            default -> Color.MEDIUMSEAGREEN;
        };
    }
}
