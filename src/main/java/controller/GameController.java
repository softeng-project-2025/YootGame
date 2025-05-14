package controller;

import model.Game;
import model.board.Board;
import model.dto.GameStateDto;
import model.dto.MessageType;
import model.dto.MoveResult;
import model.dto.NextStateHint;
import model.service.GameService;
import model.player.Player;
import model.piece.Piece;
import model.strategy.HexPathStrategy;
import model.strategy.PentagonPathStrategy;
import model.strategy.PathStrategy;
import model.strategy.SquarePathStrategy;
import model.yut.YutResult;
import view.View;

import java.util.List;
import java.util.stream.IntStream;

public class GameController {
    private GameService service;
    private final View view;
    private YutResult selectedYut;


    public GameController(View view) {
        this.view = view;
        this.view.setController(this);
        initializeGameDialog();
    }

    // 1) 설정 다이얼로그 띄우기
    public void initializeGameDialog() {
        view.showGameSetupDialog();
    }

    // 2) 사용자가 입력한 설정으로 실제 게임 시작
    public void initializeGame(int playerCount, int pieceCount, String boardType) {
        PathStrategy pathStrategy = resolvePathStrategy(boardType);
        List<Player> players = createPlayers(playerCount, pieceCount);

        Game game = new Game(new Board(pathStrategy), players);
        this.service = new GameService(game);
        service.startTurn();

        // 초기 DTO 생성·렌더
        GameStateDto dto = buildDto(null, "게임을 시작하세요.", MessageType.INFO);
        view.renderGame(dto);
    }

    // 3) 랜덤 윷 던지기
    public void onRandomThrow() {
        MoveResult result = service.throwYut();
        handleNextStateHint(result);
        view.renderGame(buildDto(result, result.yutResult().toString() + "이(가) 나왔습니다.", null));
    }

    // 4) 지정 윷 던지기 (테스트용)
    public void onDesignatedThrow(YutResult yut) {
        MoveResult result = service.throwYut(yut);
        handleNextStateHint(result);
        view.renderGame(buildDto(result, result.yutResult().toString() + "이(가) 나왔습니다.", null));
    }

    // 5) 말 선택 (뷰에서 ID를 찾는 메서드 쓰거나, Piece 객체 직접 넘겨도 됨)
    public void onSelectPieceById(int pieceId) {
        service.getGame().getPlayers().stream()
                .flatMap(p -> p.getPieces().stream())
                .filter(p -> p.getId() == pieceId)
                .findFirst()
                .ifPresentOrElse(
                        this::onSelectPiece,
                        () -> view.showMessage("선택한 말을 찾을 수 없습니다: ID=" + pieceId)
                );
    }

    // ★ 추가: SwingView에서 호출될 메서드
    public void onSelectPendingYut(YutResult yut) {
        this.selectedYut = yut;
        view.updateStatus(yut + " 선택됨. 이동할 말을 선택하세요.", MessageType.INFO);
    }

    private void onSelectPiece(Piece piece) {
        // 1) 실제 사용할 윷 결과 결정
        YutResult toUse = (selectedYut != null)
                ? selectedYut
                : service.getGame().getTurnResult().getLastResult();
        selectedYut = null;

        // 2) 이동 시도
        MoveResult result = service.selectPiece(piece, toUse);

        if (result.isGameOver()) {
            view.showWinner(result.winner());
            return;
        }

        GameStateDto dto = buildDto(
                result,
                (result.movedPiece().getId() - (piece.getOwner().getId() - 1) * piece.getOwner().getPieces().size()) + "번 말을 옮겼습니다.",
                null
        );
        view.renderGame(dto);

        // 4) 상태 전이 메시지 (턴 넘김 / 보너스 / 종료) 먼저 처리
        handleNextStateHint(result);

        System.out.println("현재 차례: " + service.getGame().getTurnManager().currentPlayer().getName());
    }

    private void handleNextStateHint(MoveResult result) {
        NextStateHint hint = result.nextStateHint();
        if (hint == null) {
            // 전이할 상태가 지정되지 않은 경우(예: 아직 선택 단계로 넘어가지 않을 때) 무시
            return;
        }

        // 턴 스킵된 경우 사용자에게 알려주기
        if (result.turnSkipped()) {
            view.showMessage("빽도가 나왔지만, 이동할 수 있는 말이 없어서 자동으로 넘겨집니다.");
        }

        switch (hint) {
            case WAITING_FOR_THROW:
                // 다음 단계 대기이니 따로 할 일 없음
                System.out.println(result.hasPendingYutResults());
                System.out.println(service.getGame().getTurnResult().getPending());
                System.out.println(service.getGame().getTurnResult().getApplied());
                view.updateStatus("윷을 던지세요.", MessageType.INFO);
                System.out.println("WAITING_FOR_THROW");
                break;
            case NEXT_TURN:
                String next = service.getGame()
                        .getTurnManager()
                        .currentPlayer()
                        .getName();
                view.showMessage("이제 " + next + " 차례입니다.");
                view.updateStatus("다음: " + next, MessageType.INFO);
                System.out.println("NEXT_TURN");
                break;
            case STAY:
                System.out.println(result.hasPendingYutResults());
                System.out.println(service.getGame().getTurnResult().getPending());
                System.out.println(service.getGame().getTurnResult().getApplied());
                view.updateStatus("같은 플레이어가 한 번 더 던집니다.", MessageType.INFO);
                System.out.println("STAY");
                break;
            case GAME_ENDED:
                Player winner = result.winner();
                view.showWinner(winner);
                break;
        }
    }

    // DTO 생성 헬퍼
    private GameStateDto buildDto(MoveResult result, String overrideMsg, MessageType overrideType) {
        String msg = overrideMsg != null
                ? overrideMsg
                : (result.isFailure() ? "오류: " + result.failType() : "이동 성공");
        MessageType type = overrideType != null
                ? overrideType
                : (result.isFailure() ? MessageType.ERROR : MessageType.INFO);
        List<Piece> selectable = service.getSelectablePieces();
        return GameStateDto.from(service.getGame(), result, selectable, msg, type);
    }

    // 보조 메서드
    private PathStrategy resolvePathStrategy(String boardType) {
        return switch (boardType.toLowerCase()) {
            case "pentagon" -> new PentagonPathStrategy();
            case "hexagon"  -> new HexPathStrategy();
            default          -> new SquarePathStrategy();
        };
    }
    private List<Player> createPlayers(int count, int pieces) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> new Player(i, "Player " + i, pieces))
                .toList();
    }

    public PathStrategy getCurrentBoardStrategy() {
        return service.getGame().getBoard().getStrategy();
    }


    public void onRestartGame() {
        // 1) 모델 초기화
        service.restartGame();
        // 2) View 초기화(UI 리셋 + 보드 클리어)
        view.resetUI();
        // 3) 새 게임 설정 다이얼로그 띄우기
        view.showGameSetupDialog();
    }

    @Deprecated
    public GameStateDto getInitialGameStateDto() {
        // 예시: 빈 보드, 버튼만 세팅한 DTO
        return GameStateDto.empty();
    }



}