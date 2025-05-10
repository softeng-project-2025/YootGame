package model.dto;

import model.Game;
import model.board.Board;
import model.piece.Piece;
import model.player.Player;
import model.yut.YutResult;

import java.util.List;

public record GameStateDto(
        Board board,
        List<Player> players,
        Player currentPlayer,
        YutResult lastYutResult,
        MoveResult lastMoveResult,
        GameMessage lastMessage,
        List<Piece> selectablePieces,
        NextStateHint nextStateHint,
        boolean gameEnded
) {

    /**
     * Game과 도메인 결과를 바탕으로 GameStateDto 생성
     */
    public static GameStateDto from(
            Game game,
            YutResult yutResult,
            MoveResult moveResult,
            GameMessage message,
            List<Piece> selectablePieces
    ) {
        return new GameStateDto(
                game.getBoard(),
                game.getPlayers(),
                game.getTurnManager().currentPlayer(),
                yutResult,
                moveResult,
                message,
                selectablePieces,
                moveResult.nextStateHint(),
                game.isFinished()
        );
    }
}
