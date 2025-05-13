package model.state;

import model.Game;
import model.dto.*;
import model.manager.CaptureManager;
import model.manager.GroupManager;
import model.manager.VictoryManager;
import model.piece.Piece;
import model.position.Position;
import model.turn.TurnResult;
import model.yut.YutResult;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// CanSelectPiece 상태: 사용자가 선택한 윷 결과를 특정 말에 적용하는 책임을 수행합니다.
public class SelectingPieceState implements CanSelectPiece {
    private final Game game;
    private final TurnResult turnResult;
    private final CaptureManager captureManager;
    private final GroupManager groupManager;

    public SelectingPieceState(Game game) {
        this.game = game;
        this.turnResult = game.getTurnResult();
        this.captureManager = new CaptureManager();
        this.groupManager = new GroupManager();
    }

    @Override
    public MoveResult handlePieceSelect(Piece piece, YutResult yut) {
        // 1) 유효성 검사
        if (piece.isFinished() || piece.getOwner() != game.getTurnManager().currentPlayer()) {
            return MoveResult.fail(yut, MoveFailType.INVALID_SELECTION);
        }
        List<YutResult> pending = turnResult.getPending();
        if (!pending.contains(yut)) {
            return MoveResult.fail(yut, MoveFailType.NO_RESULT);
        }

        // 2) 이동 전: 같은 칸에서 업힐 말 그룹 미리 계산
        List<Piece> allPieces = game.getPlayers().stream()
                .flatMap(p -> p.getPieces().stream())
                .collect(Collectors.toList());
        GroupManager.GroupKey oldKey =
                new GroupManager.GroupKey(piece.getOwner(), piece.getPosition());
        Map<GroupManager.GroupKey, List<Piece>> groupMap =
                groupManager.computeGroups(allPieces);
        List<Piece> riding =
                groupMap.getOrDefault(oldKey, List.of());

        // 3) 대표 말 이동
        turnResult.apply(yut, piece);                           // 기록만
        Position newPos = game.getBoard().movePiece(piece, yut); // 실제 위치 변경

        // 4) 그룹원도 함께 이동
        for (Piece p : riding) {
            if (p != piece) {
                game.getBoard().movePiece(p, yut);               // 실제 위치 변경
            }
        }

        // 5) 캡처 처리
        Map<Piece, List<Piece>> captures = captureManager.handleCaptures(
                List.of(piece), game.getPlayers());
        boolean didCapture = captures.containsKey(piece);

        // 6) 그룹
        Map<GroupManager.GroupKey, List<Piece>> groupMap =
                groupManager.computeGroups(allPieces);

        // 7) 승리 검사
        boolean isGameOver = VictoryManager.hasPlayerWon(piece.getOwner());
        if (isGameOver) {
            game.transitionTo(new GameOverState());
            return MoveResult.gameOver(yut, piece.getOwner())
                    .withNextStateHint(NextStateHint.GAME_ENDED);
        }

        // 8) 다음 상태 힌트 계산
        boolean bonusTurn = (yut == YutResult.YUT || yut == YutResult.MO) ^ didCapture;
        boolean hasMore = turnResult.hasPending();
        NextStateHint hint = bonusTurn ? NextStateHint.WAITING_FOR_THROW
                : hasMore  ? NextStateHint.STAY
                : NextStateHint.NEXT_TURN;

        // 9) 상태 전이 및 턴 전환
        if (hint == NextStateHint.NEXT_TURN) {
            game.startTurn();
            game.getTurnManager().nextTurn();
        }
        game.transitionTo(new WaitingForThrowState(game));

        // 8) MoveResult 반환
        return MoveResult.success(
                yut,
                didCapture,
                bonusTurn,
                game.getTurnManager().currentPlayer(),
                game,
                piece,
                hasMore,
                captures,
                groupMap
        ).withNextStateHint(hint);
    }

}


