package model.state;

import model.Game;
import model.board.Board;
import model.dto.GameMessage;
import model.dto.GameMessageFactory;
import model.dto.MessageType;
import model.dto.MoveResult;
import model.piece.Piece;
import model.piece.PieceUtil;
import model.position.Position;
import model.yut.YutQueueHandler;
import model.yut.YutResult;

import java.util.ArrayList;
import java.util.List;

public class SelectingPieceState implements GameState {

    private final Game game;

    public SelectingPieceState(Game game, YutResult result) {
        this.game = game;
        this.game.enqueueYutResult(result);
    }

    @Override
    public MoveResult handleYutThrowWithResult(YutResult result) {
        game.setLastMessage(GameMessageFactory.alreadyThrownMessage());
        return MoveResult.fail();
    }

    @Override
    public MoveResult handlePieceSelectWithResult(Piece piece) {
        if (piece.isFinished() || piece.getOwner() != game.getCurrentPlayer()) {
            game.setLastMessage(GameMessageFactory.invalidSelectionMessage());
            return MoveResult.fail();
        }

        YutResult moveResult = YutQueueHandler.dequeueResult(game);
        if (moveResult == null) {
            game.setLastMessage(GameMessageFactory.noResultMessage());
            return MoveResult.fail();
        }

        // 그룹 설정
        List<Piece> group = PieceUtil.getMovableGroup(piece, game);

        // 이동 처리
        boolean captured = game.getBoard().movePiece(piece, moveResult, game.getPlayers());

        String playerName = piece.getOwner().getName();
        if (PieceUtil.allFinished(group, game.getBoard())) {
            for (Piece p : group) {
                p.setFinished(true);
                PieceUtil.resetGroupToSelf(p);
            }
            game.setLastMessage(GameMessageFactory.goalMessage(playerName));
            return MoveResult.goal(piece.getOwner(), game);
        }

        // 추가 턴 조건
        boolean isYutOrMo = moveResult == YutResult.YUT || moveResult == YutResult.MO;
        boolean bonusTurn = (captured && !isYutOrMo) || (isYutOrMo && !captured);

        // 기본 메시지 결정
        GameMessage baseMessage = captured
                ? GameMessageFactory.captureMessage(playerName, bonusTurn)
                : isYutOrMo
                ? GameMessageFactory.yutOrMoMessage(playerName)
                : GameMessageFactory.moveMessage(playerName);

        // 다음 상태 결정
        if (game.hasPendingYutResults()) {
            game.setLastMessage(GameMessageFactory.withNextResultPrompt(baseMessage));
            return MoveResult.success(captured, false, game.isFinished() ? piece.getOwner() : null, game);
        }

        if (bonusTurn) {
            game.setState(new WaitingForThrowState(game));
            game.setLastMessage(GameMessageFactory.withThrowPrompt(baseMessage));
            return MoveResult.success(captured, true, game.isFinished() ? piece.getOwner() : null, game);
        }

        // 턴 종료
        game.nextTurn();
        game.setLastMessage(GameMessageFactory.withNextTurnPrompt(baseMessage));
        return MoveResult.success(captured, false, game.isFinished() ? piece.getOwner() : null, game);
    }

}


