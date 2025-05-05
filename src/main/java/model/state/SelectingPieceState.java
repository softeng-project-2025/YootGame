package model.state;

import model.Game;
import model.board.Board;
import model.dto.MoveResult;
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
    public MoveResult handleYutThrowWithResult(YutResult result) {
        String message = "이미 윷을 던졌습니다. 말을 선택하세요.";
        System.out.println("[WARN] " + message);

        return new MoveResult(
                message,
                false,   // captured
                game.isFinished(),
                null,    // winner
                false,   // bonusTurn
                false    // turnSkipped
        );
    }


    @Override
    public MoveResult handlePieceSelectWithResult(Piece piece) {
        if (piece.isFinished() || piece.getOwner() != game.getCurrentPlayer()) {
            return new MoveResult("잘못된 말 선택입니다.",
                    false, false, null, false, false);
        }

        List<Piece> group = new ArrayList<>();
        group.add(piece);
        for (Piece other : piece.getOwner().getPieces()) {
            if (other != piece &&
                    !other.isFinished() &&
                    other.getPosition().equals(piece.getPosition()) &&
                    other.hasMoved()) {
                group.add(other);
            }
        }

        PieceUtil.ensureGroupConsistency(group);
        Board board = game.getBoard();
        boolean captured = board.movePiece(piece, currentResult, game.getPlayers());

        Position finalPos = board.getPathStrategy().getPath().getLast(); // 마지막 위치

        boolean allAtGoal = group.stream().allMatch(p -> p.getPosition().equals(finalPos));
        if (allAtGoal) {
            group.forEach(p -> {
                p.setFinished(true);
                PieceUtil.resetGroupToSelf(p);
            });
            boolean isWin = game.checkAndHandleWinner();
            return new MoveResult(piece.getOwner().getName() + "의 말이 골인했습니다!",
                    false, isWin, game.isFinished() ? piece.getOwner() : null, false, false);
        }

        boolean isYutOrMo = currentResult == YutResult.YUT || currentResult == YutResult.MO;
        boolean bonusTurn = (captured && !isYutOrMo) || (isYutOrMo && !captured);
        boolean win = game.checkAndHandleWinner();

        String message;
        if (captured) {
            message = piece.getOwner().getName() + "이(가) 상대 말을 잡았습니다!" + (bonusTurn ? " 한 번 더 던지세요." : "");
        } else if (isYutOrMo) {
            message = piece.getOwner().getName() + "이(가) 윷 또는 모로 추가 턴을 얻었습니다.";
        } else {
            message = piece.getOwner().getName() + "이(가) 말을 이동했습니다. 턴 종료, 다음 플레이어로 넘어갑니다.";
        }

        return new MoveResult(
                message,
                captured,
                win,
                win ? piece.getOwner() : null,
                bonusTurn,
                false
        );
    }

    @Override
    public YutResult getLastYutResult() {
        return currentResult;
    }
}


