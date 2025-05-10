package controller;

import model.Game;
import model.board.Board;
import model.dto.MessageType;
import model.dto.MoveResult;
import model.service.GameService;
import model.player.Player;
import model.piece.Piece;
import model.strategy.HexPathStrategy;
import model.strategy.PathStrategy;
import model.strategy.PentagonPathStrategy;
import model.strategy.SquarePathStrategy;
import model.yut.YutResult;
import view.View;

import java.util.List;
import java.util.stream.IntStream;

// GameController: 사용자 입력을 받아 GameService와 View를 연결합니다.
public class GameController {

    private GameService service;
    private final View view;


    public GameController(View view) {
        this.view = view;
        this.view.setController(this);
    }

    // showDialog에서 받은 파라미터로 실제 모델을 생성
    public void initializeGame(int playerCount, int pieceCount, String boardType) {
        PathStrategy strat = resolvePathStrategy(boardType);
        List<Player> players = createPlayers(playerCount, pieceCount);

        Game game = new Game(new Board(strat), players);
        this.service = new GameService(game);
        service.startTurn();

        view.renderGame(service.getGame());
        view.updateYutResult(null);
        view.updateStatus("게임을 시작하세요.", MessageType.INFO);  // 초기 안내


    }

    private PathStrategy resolvePathStrategy(String boardType) {
        return switch (boardType.toLowerCase()) {
            case "pentagon" -> new PentagonPathStrategy();
            case "hexagon"  -> new HexPathStrategy();
            default          -> new SquarePathStrategy();
        };
    }

    private List<Player> createPlayers(int playerCount, int pieceCount) {
        return IntStream.rangeClosed(1, playerCount)
                .mapToObj(i -> new Player(i, "Player " + i, pieceCount))
                .toList();
    }

    // 랜덤 윷 던지기 처리
    public void onRandomThrow() {
        MoveResult result = service.throwYut();
        handleMoveResult(result);
    }

    // 지정 윷 던지기 요청 처리
    public void onDesignatedThrow(YutResult yut) {
        MoveResult result = service.throwYut(yut);
        handleMoveResult(result);
    }

    // 사용자가 선택한 말에 대해 이동 처리
    public void onSelectPiece(Piece piece) {
        YutResult pending = service.getGame().getTurnResult().getLastResult();
        MoveResult result = service.selectPiece(piece, pending);
        handleMoveResult(result);
    }

    // MoveResult에 따른 공통 View 업데이트
    private void handleMoveResult(MoveResult result) {
        // 1) 윷 결과 표시
        view.updateYutResult(result.yutResult());

        // 2) 게임판 갱신
        view.renderGame(service.getGame());

        // 3) 상태 메시지
        String status = result.isFailure()
                ? "오류: " + result.failType()
                : "이동 성공";
        view.updateStatus(status,
                result.isFailure() ? MessageType.ERROR : MessageType.INFO);


        // 4) 선택 가능한 말 표시
        if (!result.isFailure() && result.hasPendingYutResults()) {
            List<Piece> options = service.getSelectablePieces();
            view.showSelectablePieces(options);
        }


        // 5) 게임 종료 처리
        if (result.isGameOver()) {
            Player winner = result.winner();
            view.showWinner(winner);
            view.promptRestart(this);
        }
    }

}
