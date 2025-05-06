package model.state;

import model.Game;
import model.board.Board;
import model.dto.GameMessage;
import model.dto.MessageType;
import model.dto.MoveResult;
import model.piece.Piece;
import model.piece.PieceUtil;
import model.position.Position;
import model.yut.YutResult;

import java.util.ArrayList;
import java.util.List;

public class SelectingPieceState implements GameState {

    private final Game game;

    public SelectingPieceState(Game game, YutResult result) {
        this.game = game;

    }

    @Override
    public MoveResult handleYutThrowWithResult(YutResult result) {
        game.setLastMessage(new GameMessage("이미 윷을 던졌습니다. 말을 선택하세요.", MessageType.WARN));

        return new MoveResult(
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
            game.setLastMessage(new GameMessage("잘못된 말 선택입니다.", MessageType.WARN));
            return new MoveResult(false, false, null, false, false);
        }

        YutResult moveResult = game.dequeueYutResult();
        if (moveResult == null) {
            game.setLastMessage(new GameMessage("적용할 윷 결과가 없습니다.", MessageType.WARN));
            return new MoveResult( false, false, null, false, false);
        }

        // 그룹 설정
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

        // 이동 처리
        Board board = game.getBoard();
        boolean captured = board.movePiece(piece, moveResult, game.getPlayers());

        Position finalPos = board.getPathStrategy().getPath().get(
                board.getPathStrategy().getPath().size() - 1);

        boolean allAtGoal = group.stream().allMatch(p -> p.getPosition().equals(finalPos));
        if (allAtGoal) {
            for (Piece p : group) {
                p.setFinished(true);
                PieceUtil.resetGroupToSelf(p);
            }

            game.setLastMessage(new GameMessage(piece.getOwner().getName() + "의 말이 골인했습니다!", MessageType.INFO));
            return new MoveResult(
                    false, game.checkAndHandleWinner(), game.isFinished() ? piece.getOwner() : null, false, false);
        }

        // 추가 턴 조건
        boolean isYutOrMo = moveResult == YutResult.YUT || moveResult == YutResult.MO;
        boolean bonusTurn = (captured && !isYutOrMo) || (isYutOrMo && !captured);

        String message;
        if (captured) {
            message = piece.getOwner().getName() + "이(가) 상대 말을 잡았습니다!" + (bonusTurn ? " 한 번 더 던지세요." : "");
        } else if (isYutOrMo) {
            message = piece.getOwner().getName() + "이(가) 윷 또는 모로 추가 턴을 얻었습니다.";
        } else {
            message = piece.getOwner().getName() + "이(가) 말을 이동했습니다.";
        }

        // 다음 상태 결정
        if (game.hasPendingYutResults()) {
            // 아직 큐에 결과 남아 있음 → SelectingPieceState 유지
            game.setLastMessage(new GameMessage(message + " 다음 결과를 적용할 말을 선택하세요.", MessageType.INFO));
            return new MoveResult(captured, game.checkAndHandleWinner(), game.isFinished() ? piece.getOwner() : null, false, false);
        } else if (bonusTurn) {
            game.setState(new WaitingForThrowState(game));
            game.setLastMessage(new GameMessage(message + " 추가 턴입니다. 윷을 던지세요.", MessageType.INFO));
            return new MoveResult(captured, game.checkAndHandleWinner(), game.isFinished() ? piece.getOwner() : null, true, false);
        } else {
            game.nextTurn();
            game.setLastMessage(new GameMessage(message + " 다음 플레이어의 차례입니다.", MessageType.INFO));
            return new MoveResult(captured, game.checkAndHandleWinner(), game.isFinished() ? piece.getOwner() : null, false, false);
        }
    }

}


