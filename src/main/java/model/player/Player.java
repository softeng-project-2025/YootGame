package model.player;

import java.util.ArrayList;
import java.util.List;
import model.board.Board;
import model.position.Position;

import model.piece.Piece;

public class Player {
    private final String name;
    private final List<Piece> pieces;

    public Player(String name, int pieceCount, Board board) {
        this.name = name;
        this.pieces = new ArrayList<>();
        Position startPos = board.getPathStrategy().getPath().get(0); // 시작 위치

        for (int i = 0; i < pieceCount; i++) {
            pieces.add(new Piece(this, i, startPos));
        }
    }

    public String getName() {
        return name;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    // 특정 말을 선택했을 때 해당 말을 반환 (id로 찾기)
    public Piece getPieceById(int id) {
        return pieces.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // 모든 말을 완주했는지 확인 (승리 조건)
    public boolean hasFinishedAllPieces() {
        return pieces.stream().allMatch(Piece::isFinished);
    }

    // 움직일 수 있는 말이 존재하는지
    public boolean hasMovablePieces() {
        return pieces.stream().anyMatch(p -> !p.isFinished());
    }


}