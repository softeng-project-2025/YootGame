package model.dto;

import model.Game;
import model.piece.Piece;
import model.player.Player;

public record MoveResult(
        boolean captured,
        boolean gameEnded,
        Player winner,
        boolean bonusTurn,
        boolean turnSkipped,
        Piece movedPiece,
        MoveFailType failType,
        boolean hasPendingYutResults
) {
    // 실패 케이스
    public static MoveResult fail(MoveFailType reason) {
        return new MoveResult(false, false, null, false, false, null, reason, false);
    }

    // 골인 케이스
    public static MoveResult goal(Player winner, Game game) {
        return new MoveResult(false, game.isFinished(), winner, false, false, null, null, false);
    }

    public static MoveResult goal(Player winner, Game game, Piece movedPiece) {
        return new MoveResult(false, game.isFinished(), winner, false, false, movedPiece, null, false);
    }

    // 정상 이동 - 기본 (movedPiece 없이)
    public static MoveResult success(boolean captured, boolean bonusTurn, Player winner, Game game) {
        return new MoveResult(captured, game.isFinished(), winner, bonusTurn, false, null, null, false);
    }

    // 정상 이동 - movedPiece 포함
    public static MoveResult success(boolean captured, boolean bonusTurn, Player winner, Game game, Piece movedPiece) {
        return new MoveResult(captured, game.isFinished(), winner, bonusTurn, false, movedPiece, null, false);
    }

    // 정상 이동 - movedPiece + 남은 윷 결과 여부 포함
    public static MoveResult success(boolean captured, boolean bonusTurn, Player winner, Game game, Piece movedPiece, boolean hasPendingYutResults) {
        return new MoveResult(captured, game.isFinished(), winner, bonusTurn, false, movedPiece, null, hasPendingYutResults);
    }

    // 일반 이동 (보통의 턴 종료, bonusTurn = false, movedPiece = null)
    public static MoveResult normal(Player player, Game game) {
        return success(false, false, player, game);
    }

    // 일반 이동 (movedPiece 포함)
    public static MoveResult normal(Player player, Game game, Piece movedPiece) {
        return success(false, false, player, game, movedPiece);
    }

    // 이동할 말이 없어서 턴 스킵
    public static MoveResult skipped() {
        return new MoveResult(false, false, null, false, true, null, null, false);
    }

    // 게임 종료
    public static MoveResult gameOver(Player winner) {
        return new MoveResult(false, true, winner, false, false, null, null, false);
    }

    // 유틸
    public boolean isFailure() {
        return failType != null;
    }

    public boolean isSuccess() {
        return failType == null;
    }

}