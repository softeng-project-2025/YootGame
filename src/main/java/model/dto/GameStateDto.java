package model.dto;

import model.Game;
import model.piece.Piece;
import model.yut.YutResult;
import java.util.List;
import java.util.stream.Collectors;

public record GameStateDto(
        BoardDto board,
        List<PlayerDto> players,
        PieceDto lastMovedPiece,
        String messageText,
        MessageType messageType,
        boolean gameOver
) {
    /**
     * Game 도메인 객체와 연산 결과를 바탕으로 DTO 생성
     */
    public static GameStateDto fromGame(
            Game game,
            YutResult yutResult,
            MoveResult moveResult,
            List<Piece> selectablePieces
    ) {
        BoardDto boardDto = BoardDto.from(game.getBoard(), selectablePieces);
        List<PlayerDto> playerDtos = game.getPlayers().stream()
                .map(p -> PlayerDto.from(p, p.equals(game.getCurrentPlayer())))
                .collect(Collectors.toList());

        PieceDto moved = null;
        if (moveResult != null && moveResult.movedPiece() != null) {
            moved = PieceDto.from(moveResult.movedPiece(), false);
        }

        String text = moveResult != null ? moveResult.getMessage() : yutResult.getName();
        MessageType type = moveResult != null ? moveResult.getMessageType() : MessageType.INFO;

        return new GameStateDto(
                boardDto,
                playerDtos,
                moved,
                text,
                type,
                game.isFinished()
        );
    }
}
