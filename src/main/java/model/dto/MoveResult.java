package model.dto;

import model.Game;
import model.manager.GroupManager;
import model.piece.Piece;
import model.player.Player;
import model.yut.YutResult;

import java.util.List;
import java.util.Map;

// 이동 결과를 나타내는 레코드입니다.
// YutResult, 캡처 여부, 게임 종료 여부, 승자, 보너스 턴 여부, 이동된 말, 실패 사유, 대기 중인 윷 결과 여부, 다음 상태 힌트를 포함합니다.
public record MoveResult(
        YutResult yutResult,
        boolean captured,
        boolean gameEnded,
        Player winner,
        boolean bonusTurn,
        boolean turnSkipped,
        Piece movedPiece,
        MoveFailType failType,
        boolean hasPendingYutResults,
        NextStateHint nextStateHint,
        Map<Piece, List<Piece>> captureMap,
        Map<GroupManager.GroupKey, List<Piece>> groupMap
) {
    // 실패 케이스
    public static MoveResult fail(YutResult yut, MoveFailType reason) {
        return new MoveResult(
                yut,
                false,
                false,
                null,
                false,
                false,
                null,
                reason,
                false,
                NextStateHint.SELECTING_PIECE,
                Map.of(),
                Map.of()
        );
    }

    // 골인 케이스
    public static MoveResult goal(YutResult yut, Player winner, Game game) {
        return new MoveResult(
                yut,
                false,
                game.isFinished(),
                winner,
                false,
                false,
                null,
                null,
                false,
                NextStateHint.GAME_ENDED,
                Map.of(),
                Map.of()
        );
    }

    public static MoveResult goal(YutResult yut, Player winner, Game game, Piece movedPiece) {
        return new MoveResult(
                yut,
                false,
                game.isFinished(),
                winner,
                false,
                false,
                movedPiece,
                null,
                false,
                NextStateHint.GAME_ENDED,
                Map.of(),
                Map.of()
        );
    }

    // 정상 이동 - 기본
    public static MoveResult success(YutResult yut, boolean captured, boolean bonusTurn, Player winner, Game game) {
        return new MoveResult(
                yut,
                captured,
                game.isFinished(),
                winner,
                bonusTurn,
                false,
                null,
                null,
                true,
                hintFor(bonusTurn, true, game.isFinished()),
                Map.of(),
                Map.of()
        );
    }

    // 정상 이동 - movedPiece 포함
    public static MoveResult success(YutResult yut, boolean captured, boolean bonusTurn, Player winner, Game game, Piece movedPiece) {
        return new MoveResult(
                yut,
                captured,
                game.isFinished(),
                winner,
                bonusTurn,
                false,
                movedPiece,
                null,
                true,
                hintFor(bonusTurn, false, game.isFinished()),
                Map.of(),
                Map.of()

        );
    }

    // 정상 이동 - movedPiece + hasPendingYutResults 포함
    public static MoveResult success(YutResult yut, boolean captured, boolean bonusTurn, Player winner, Game game, Piece movedPiece, boolean hasPendingYutResults, Map<Piece, List<Piece>> captureMap,
                                     Map<GroupManager.GroupKey, List<Piece>> groupMap) {
        return new MoveResult(
                yut,
                captured,
                game.isFinished(),
                winner,
                bonusTurn,
                false,
                movedPiece,
                null,
                hasPendingYutResults,
                hintFor(bonusTurn, hasPendingYutResults, game.isFinished()),
                captureMap,
                groupMap
        );
    }

    // 이동할 수 없어 턴 스킵
    public static MoveResult skipped(YutResult yut) {
        return new MoveResult(
                yut,
                false,
                false,
                null,
                false,
                true,
                null,
                null,
                false,
                NextStateHint.NEXT_TURN,
                Map.of(),
                Map.of()
        );
    }

    // 게임 종료
    public static MoveResult gameOver(YutResult yut, Player winner) {
        return new MoveResult(
                yut,
                false,
                true,
                winner,
                false,
                false,
                null,
                null,
                false,
                NextStateHint.GAME_ENDED,
                Map.of(),
                Map.of()
        );
    }

    /** 실패 여부 */
    public boolean isFailure() {
        return failType != null;
    }

    /** 성공 여부 */
    public boolean isSuccess() {
        return failType == null;
    }

    /** 게임 종료 여부 */
    public boolean isGameOver() {
        return gameEnded;
    }

    /** 다음 상태 힌트 */
    public NextStateHint getNextStateHint() {
        return nextStateHint;
    }

    public MoveResult withNextStateHint(NextStateHint hint) {
        return new MoveResult(yutResult, captured, gameEnded, winner, bonusTurn, turnSkipped, movedPiece, failType, hasPendingYutResults, hint, captureMap,
                groupMap);
    }

    private static NextStateHint hintFor(boolean bonusTurn, boolean hasPendingYuts, boolean isFinished) {
        if (isFinished) {
            return NextStateHint.GAME_ENDED;
        } else if (bonusTurn) {
            return NextStateHint.WAITING_FOR_THROW;
        } else if (hasPendingYuts) {
            return NextStateHint.SELECTING_PIECE;
        } else {
            return NextStateHint.NEXT_TURN;
        }
    }
    /** captureMap만 교체한 새 인스턴스를 반환 */
    public MoveResult withCaptureMap(Map<Piece, List<Piece>> newCaptureMap) {
        return new MoveResult(
                yutResult,
                captured,
                gameEnded,
                winner,
                bonusTurn,
                turnSkipped,
                movedPiece,
                failType,
                hasPendingYutResults,
                nextStateHint,
                newCaptureMap,
                this.groupMap
        );
    }

    /** groupMap만 교체한 새 인스턴스를 반환 */
    public MoveResult withGroupMap(Map<GroupManager.GroupKey, List<Piece>> newGroupMap) {
        return new MoveResult(
                yutResult,
                captured,
                gameEnded,
                winner,
                bonusTurn,
                turnSkipped,
                movedPiece,
                failType,
                hasPendingYutResults,
                nextStateHint,
                this.captureMap,
                newGroupMap
        );
    }
}