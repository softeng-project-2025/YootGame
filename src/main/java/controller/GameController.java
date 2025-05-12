package controller;

import model.Game;
import model.board.Board;
import model.dto.GameStateDto;
import model.dto.MessageType;
import model.dto.MoveResult;
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
        view.renderGame(buildDto(result, null, null));
    }

    // 4) 지정 윷 던지기 (테스트용)
    public void onDesignatedThrow(YutResult yut) {
        MoveResult result = service.throwYut(yut);
        view.renderGame(buildDto(result, null, null));
    }

    // 5) 말 선택 (뷰에서 ID를 찾는 메서드 쓰거나, Piece 객체 직접 넘겨도 됨)
    public void onSelectPieceById(int pieceId) {
        service.getGame().getPlayers().stream()
                .flatMap(p -> p.getPieces().stream())
                .filter(p -> p.getId() == pieceId)
                .findFirst()
                .ifPresentOrElse(
                        piece -> onSelectPiece(piece),
                        () -> view.showMessage("선택한 말을 찾을 수 없습니다: ID=" + pieceId)
                );
    }
    private void onSelectPiece(Piece piece) {
        MoveResult result = service.selectPiece(
                piece,
                service.getGame().getTurnResult().getLastResult()
        );
        view.renderGame(buildDto(result, null, null));
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
        service.restartGame();
        view.resetUI();
    }

    public GameStateDto getInitialGameStateDto() {
        // 예시: 빈 보드, 버튼만 세팅한 DTO
        return GameStateDto.empty();
    }

}