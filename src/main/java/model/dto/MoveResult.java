package model.dto;

import model.Game;
import model.piece.Piece;
import model.player.Player;
import model.state.WaitingForThrowState;

public record MoveResult(
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
    public static MoveResult fail(MoveFailType reason) {
        return new MoveResult(false, false, null, false, false, null, reason, false, NextStateHint.STAY);
    }

    // 골인 케이스
    public static MoveResult goal(Player winner, Game game) {
        return new MoveResult(false, game.isFinished(), winner, false, false, null, null, false, NextStateHint.GAME_ENDED);
    }

    public static MoveResult goal(Player winner, Game game, Piece movedPiece) {
        return new MoveResult(false, game.isFinished(), winner, false, false, movedPiece, null, false, NextStateHint.GAME_ENDED);
    }

    // 정상 이동 - 기본
    public static MoveResult success(boolean captured, boolean bonusTurn, Player winner, Game game) {
        return new MoveResult(captured, game.isFinished(), winner, bonusTurn, false, null, null, false, hintFor(bonusTurn, false, false));
    }

    // 정상 이동 - movedPiece 포함
    public static MoveResult success(boolean captured, boolean bonusTurn, Player winner, Game game, Piece movedPiece) {
        return new MoveResult(captured, game.isFinished(), winner, bonusTurn, false, movedPiece, null, false, hintFor(bonusTurn, false, false));
    }

    // 정상 이동 - movedPiece + hasPendingYutResults 포함
    public static MoveResult success(boolean captured, boolean bonusTurn, Player winner, Game game, Piece movedPiece, boolean hasPendingYutResults) {
        return new MoveResult(captured, game.isFinished(), winner, bonusTurn, false, movedPiece, null, hasPendingYutResults, hintFor(bonusTurn, hasPendingYutResults, false));
    }

    // 일반 이동
    public static MoveResult normal(Player player, Game game) {
        return success(false, false, player, game);
    }

    public static MoveResult normal(Player player, Game game, Piece movedPiece) {
        return success(false, false, player, game, movedPiece);
    }

    // 이동할 수 없어 턴 스킵
    public static MoveResult skipped() {
        return new MoveResult(false, false, null, false, true, null, null, false, NextStateHint.NEXT_TURN);
    }

    // 게임 종료
    public static MoveResult gameOver(Player winner) {
        return new MoveResult(false, true, winner, false, false, null, null, false, NextStateHint.GAME_ENDED);
    }

    // 유틸
    public boolean isFailure() {
        return failType != null;
    }

    public boolean isSuccess() {
        return failType == null;
    }

    // 내부 헬퍼
    private static NextStateHint hintFor(boolean bonusTurn, boolean hasPendingYuts, boolean isFinished) {
        // 보너스 턴 > 큐 있음 > 기본
        if (isFinished) return NextStateHint.GAME_ENDED;
        if (bonusTurn) return NextStateHint.WAITING_FOR_THROW;
        if (hasPendingYuts) return NextStateHint.STAY;
        return NextStateHint.NEXT_TURN;
    }

    public MoveResult withNextStateHint(NextStateHint hint) {
        return new MoveResult(captured, gameEnded, winner, bonusTurn, turnSkipped, movedPiece, failType, hasPendingYutResults, hint);
    }
}