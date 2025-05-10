package model.dto;

import model.piece.Piece; /**
 * View 렌더링을 위한 경량 DTO 모음
 */

public record PieceDto(
        int id,
        int x,
        int y,
        boolean selectable
) {
    public static PieceDto from(Piece piece, boolean selectable) {
        var pos = piece.getPosition();
        return new PieceDto(
                piece.getId(),
                pos.x(),
                pos.y(),
                selectable
        );
    }
}
