package model.state;

import model.Game;
import model.piece.Piece;
import model.yut.YutResult;

public class WaitingForThrowState implements GameState {

    private final Game game;
    private YutResult lastResult;

    public WaitingForThrowState(Game game) {
        this.game = game;
    }

    @Override
    public void handleYutThrow(YutResult result) {
        this.lastResult = result;
        System.out.println("[INFO] " + game.getCurrentPlayer().getName() + " 이(가) 윷을 던졌습니다: " + result);

        // TODO: 던진 결과 저장, 처리 등 로직 연결
        // 상태 전환: 다음은 말 선택 상태로
        game.setState(new SelectingPieceState(game, result));
    }

    @Override
    public void handlePieceSelect(Piece piece) {
        // 아직 윷을 안 던졌는데 말을 고르려는 상황
        System.out.println("[WARN] 아직 윷을 던지지 않았습니다!");
    }

    @Override
    public YutResult getLastYutResult() {
        return lastResult;
    }
}