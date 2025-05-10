package model.dto;

import model.Game;
import model.piece.Piece;
import model.player.Player;
import model.yut.YutResult;

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
        NextStateHint nextStateHint
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
                NextStateHint.STAY
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
                NextStateHint.GAME_ENDED
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
                NextStateHint.GAME_ENDED
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
                false,
                hintFor(bonusTurn, false, game.isFinished())
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
                false,
                hintFor(bonusTurn, false, game.isFinished())
        );
    }

    // 정상 이동 - movedPiece + hasPendingYutResults 포함
    public static MoveResult success(YutResult yut, boolean captured, boolean bonusTurn, Player winner, Game game, Piece movedPiece, boolean hasPendingYutResults) {
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
                hintFor(bonusTurn, hasPendingYutResults, game.isFinished())
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
                NextStateHint.NEXT_TURN
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
                NextStateHint.GAME_ENDED
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
        return new MoveResult(yutResult, captured, gameEnded, winner, bonusTurn, turnSkipped, movedPiece, failType, hasPendingYutResults, hint);
    }

    private static NextStateHint hintFor(boolean bonusTurn, boolean hasPendingYuts, boolean isFinished) {
        if (isFinished) return NextStateHint.GAME_ENDED;
        if (bonusTurn) return NextStateHint.WAITING_FOR_THROW;
        if (hasPendingYuts) return NextStateHint.STAY;
        return NextStateHint.NEXT_TURN;
    }
}