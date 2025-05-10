package model.dto;

import model.board.Board;
import model.piece.Piece;
import java.util.List;
import java.util.stream.Collectors;

public record BoardDto(
        List<PieceDto> pieces
) {
    public static BoardDto from(Board board, List<Piece> selectable) {
        return new BoardDto(
                board.getAllPieces().stream()
                        .map(p -> PieceDto.from(p, selectable.contains(p)))
                        .collect(Collectors.toList())
        );
    }
}