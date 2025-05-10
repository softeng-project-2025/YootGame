package model.player;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import model.board.Board;
import model.position.Position;

import model.piece.Piece;

// Player: 플레이어 정보와 소유 말(Piece) 리스트를 관리합니다.
public class Player {
    private final int id;
    private final String name;
    private final List<Piece> pieces;

    // 생성자: 플레이어 ID, 이름, 말 개수, 시작 보드를 받아 말 리스트 초기화
    public Player(int id, String name, int pieceCount, Board board) {
        this.id = id;
        this.name = Objects.requireNonNull(name);

        // 각 말별 분산된 시작 위치 계산
        // 가로 위치는 플레이어 ID에 따라, 세로 위치는 말 번호별로 오프셋 적용
        int baseX = 1200 - (4 - id) * 100;
        int baseY = 200;
        int offsetY = 100;

        this.pieces = IntStream.rangeClosed(1, pieceCount)
                .mapToObj(i -> {
                    Position startPos = new Position(
                            /* index */ 0,
                            /* x */ baseX,
                            /* y */ baseY + (i - 1) * offsetY
                    );
                    return new Piece(this, i, startPos);
                })
                .collect(Collectors.toList());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // 소유한 말 리스트 복사본 반환
    public List<Piece> getPieces() {
        return List.copyOf(pieces);
    }

    // 모든 말이 완주했는지 확인 (승리 조건)
    public boolean hasAllPiecesFinished() {
        return pieces.stream().allMatch(Piece::isFinished);
    }

    // 움직일 수 있는 말이 하나라도 있는지 확인
    public boolean hasMovablePieces() {
        return pieces.stream().anyMatch(p -> !p.isFinished());
    }

    // ID에 해당하는 말 조회
    public Piece getPieceById(int pieceId) {
        return pieces.stream()
                .filter(p -> p.getId() == pieceId)
                .findFirst()
                .orElse(null);
    }

    //모든 말 리스트 반환
    public List<Piece> getAllPieces() {
        return getPieces();
    }


}