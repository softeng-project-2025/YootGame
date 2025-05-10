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
    // 화면 배치 관련 상수
    private static final int BASE_X_OFFSET     = 1200;
    private static final int MAX_PLAYER_COUNT  = 4;
    private static final int PLAYER_SPACING_X  = 100;
    private static final int BASE_Y_OFFSET     = 200;
    private static final int PIECE_SPACING_Y   = 100;

    private final int id;
    private final String name;
    private final List<Piece> pieces;

    // 생성자: 플레이어 ID, 이름, 말 개수, 시작 보드를 받아 말 리스트 초기화
    public Player(int id, String name, int pieceCount) {
        this.id = id;
        this.name = Objects.requireNonNull(name);

        // 각 말별 분산된 시작 위치 계산
        // 가로 위치는 플레이어 ID에 따라, 세로 위치는 말 번호별로 오프셋 적용
        // 각 말별 분산된 시작 위치 계산
        int baseX = BASE_X_OFFSET - (MAX_PLAYER_COUNT - id) * PLAYER_SPACING_X;
        int baseY = BASE_Y_OFFSET;

        this.pieces = IntStream.rangeClosed(1, pieceCount)
                .mapToObj(i -> {
                    Position startPos = new Position(
                            /* index */ 0,
                            /* x */ baseX,
                            /* y */ baseY + (i - 1) * PIECE_SPACING_Y
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


}