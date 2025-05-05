package model.state;

import model.Game;
import model.board.Board;
import model.piece.Piece;
import model.piece.PieceUtil;
import model.position.Position;
import model.yut.YutResult;

import java.util.ArrayList;
import java.util.List;

public class SelectingPieceState implements GameState {

    private final Game game;
    private final YutResult currentResult;

    public SelectingPieceState(Game game, YutResult result) {
        this.game = game;
        this.currentResult = result;
    }

    @Override
    public void handleYutThrow(YutResult result) {
        // 이미 던졌기 때문에 또 던지면 안 됨
        System.out.println("[WARN] 이미 윷을 던졌습니다. 말을 선택하세요.");
    }

    @Override
    public void handlePieceSelect(Piece piece) {

        if (piece.isFinished() || piece.getOwner() != game.getCurrentPlayer()) {
            System.out.println("[WARN] 잘못된 말 선택입니다.");
            return;
        }

        System.out.println("[INFO] " + piece.getOwner().getName() + " 이(가) 말 " + piece.getId() + "을(를) 이동합니다.");

        // 이동 직전: 같은 위치의 같은 플레이어 말이 있는지 확인
        List<Piece> group = new ArrayList<>();
        group.add(piece);

        for (Piece other : piece.getOwner().getPieces()) {
            if (other != piece &&
                    !other.isFinished() &&
                    other.getPosition().equals(piece.getPosition()) &&
                    other.hasMoved()) { // 이동한 적 있는 말만 그룹 허용
                group.add(other);
            }
        }

        PieceUtil.ensureGroupConsistency(group);

        // 이동 처리
        Board board = game.getBoard();
        boolean captured = board.movePiece(piece, currentResult, game.getPlayers());

        // 도착 위치가 끝이라면 완료 처리
        Position finalPos = board.getPathStrategy().getPath().get(
                board.getPathStrategy().getPath().size() - 1);

        boolean allAtGoal = group.stream()
                .allMatch(p -> p.getPosition().equals(finalPos));


        if (allAtGoal) {
            for (Piece p : group) { // 그룹 상태에서 함께 골인
                p.setFinished(true);
                PieceUtil.resetGroupToSelf(p); // 완주 시 그룹 해제
            }
        }

        // 말 완주 후 게임 종료 조건 확인
        if (game.checkAndHandleWinner()) {
            return;
        }

        // 윷 or 모 or 잡기 → 한 번 더 턴
        if (currentResult == YutResult.YUT || currentResult == YutResult.MO || captured) {
            game.setState(new WaitingForThrowState(game));
            System.out.println("[INFO] 추가 턴! (윷/모 또는 말 잡기)");
        } else {
            game.nextTurn(); // 턴 종료 및 다음 플레이어로 전환
        }
    }
    @Override
    public YutResult getLastYutResult() {
        return currentResult;
    }
}
