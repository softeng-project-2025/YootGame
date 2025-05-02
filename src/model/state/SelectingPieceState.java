package model.state;

import model.Game;
import model.board.Board;
import model.piece.Piece;
import model.yut.YutResult;

public class SelectingPieceState implements GameState {

    private Game game;
    private YutResult result;

    public SelectingPieceState(Game game, YutResult result) {
        this.game = game;
        this.result = result;
    }

    @Override
    public void handleYutThrow(YutResult result) {
        // 이미 던졌기 때문에 또 던지면 안 됨
        System.out.println("[WARN] 이미 윷을 던졌습니다. 말을 선택하세요.");
    }

    @Override
    public void handlePieceSelect(Piece piece) {
        if (piece.isFinished()) {
            System.out.println("[WARN] 이미 완주한 말입니다.");
            return;
        }

        if (piece.getOwner() != game.getCurrentPlayer()) {
            System.out.println("[WARN] 자신의 말만 이동할 수 있습니다.");
            return;
        }

        System.out.println("[INFO] " + piece.getOwner().getName() + " 이(가) 말 " + piece.getId() + "을(를) 이동합니다.");

        // 이동 처리
        Board board = game.getBoard();
        board.movePiece(piece, result);

        // 도착 위치가 끝이라면 완료 처리
        if (/* 도착 위치가 끝인지 판별하는 조건 필요 */ false) {
            piece.setFinished(true);
        }

        // 윷 or 모 → 한 번 더 던질 수 있음
        if (result == YutResult.YUT || result == YutResult.MO) {
            System.out.println("[INFO] 윷/모! 한 번 더 던질 수 있습니다.");
            game.setState(new WaitingForThrowState(game));
        } else {
            game.nextTurn(); // 턴 종료 및 다음 플레이어로 전환
        }
    }
}
