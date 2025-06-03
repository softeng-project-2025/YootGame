package model;// groupingTest.java
//  ──────────────────────────────────────────────────────────────
//  GroupManager.computeGroups(...) 검증용 단위 테스트
//  모든 케이스는 제공된 모델 파일만 사용하며, 외부 의존성 없음.
//  ──────────────────────────────────────────────────────────────
import model.board.Board;
import model.manager.GroupManager;
import model.manager.GroupManager.GroupKey;
import model.piece.Piece;
import model.player.Player;
import model.position.Position;
import model.strategy.HexPathStrategy;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GroupTest.java
 *
 * < 시나리오 목록 >
 *  1) 모든 말이 다른 칸 → 그룹화 없음
 *  2) 같은 플레이어 2말이 같은 칸 → 하나의 그룹(크기 2)
 *  3) 같은 플레이어 여러 위치에 묶음 → 위치별 그룹 분리
 *  4) 한 칸에 3말 → 그룹 크기 3
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
class GroupTest {

    private Board board;
    private GroupManager groupManager;
    private Player p1, p2;

    @BeforeEach
    void setUp() {
        board        = new Board(new HexPathStrategy());
        groupManager = new GroupManager();

        // 플레이어 1 · 2 (각 3말)
        p1 = new Player(1, "RED", 3);
        p2 = new Player(2, "BLUE", 3);

        // 각 말에 커스텀 경로 초기화
        p1.getPieces().forEach(pc -> pc.resetToStart(board));
        p2.getPieces().forEach(pc -> pc.resetToStart(board));
    }

    /* ---------- 공용 헬퍼 ---------- */

    /** 경로 index 기준으로 말 이동(실제 step 계산 생략) */
    private void movePieceToIndex(Piece piece, int index) {
        Position target = board.getStrategy().getPath()
                .stream()
                .filter(p -> p.index() == index)
                .findFirst()
                .orElseThrow();
        piece.moveTo(target, 0);          // step=0 → pathIndex 자동 계산
    }

    /* ---------- 시나리오 1 ---------- */
    @Test @DisplayName("모든 말이 다른 칸 → 그룹화 없음")
    void noGroupingWhenAllSeparate() {
        List<Piece> pieces = List.of(
                p1.getPieces().get(0),
                p1.getPieces().get(1),
                p2.getPieces().get(0),
                p2.getPieces().get(1)
        );
        int[] idx = {5, 10, 15, 20};
        for (int i = 0; i < pieces.size(); i++) movePieceToIndex(pieces.get(i), idx[i]);

        Map<GroupKey, List<Piece>> map = groupManager.computeGroups(pieces);

        assertEquals(pieces.size(), map.size());
        map.values().forEach(list -> assertEquals(1, list.size()));
    }

    /* ---------- 시나리오 2 ---------- */
    @Test @DisplayName("같은 플레이어 2말이 같은 칸 → 하나의 그룹(크기 2)")
    void twoPiecesStackIntoOneGroup() {
        Piece a = p1.getPieces().get(0);
        Piece b = p1.getPieces().get(1);
        Piece c = p2.getPieces().get(0);

        movePieceToIndex(a, 5);
        movePieceToIndex(b, 5);
        movePieceToIndex(c, 10);

        Map<GroupKey, List<Piece>> map = groupManager.computeGroups(List.of(a, b, c));

        assertEquals(2, map.size());
        GroupKey redKey = new GroupKey(p1, a.getPosition());
        assertEquals(2, map.get(redKey).size());
    }

    /* ---------- 시나리오 3 ---------- */
    @Test @DisplayName("같은 플레이어 여러 위치에 묶음 → 위치별 그룹 분리")
    void sameOwnerDifferentPositionsGroupedSeparately() {
        Piece a = p1.getPieces().get(0);
        Piece b = p1.getPieces().get(1);
        Piece c = p1.getPieces().get(2);

        movePieceToIndex(a, 5);
        movePieceToIndex(b, 5);
        movePieceToIndex(c, 12);

        Map<GroupKey, List<Piece>> map = groupManager.computeGroups(List.of(a, b, c));

        assertEquals(2, map.size());
        List<Integer> sizes = map.values().stream().map(List::size).collect(Collectors.toList());
        assertTrue(sizes.contains(2) && sizes.contains(1));
    }


    /* ---------- 시나리오 4 ---------- */
    @Test @DisplayName("한 칸에 3말 → 그룹 크기 3")
    void threePiecesStack() {
        Piece pA = p1.getPieces().get(0);
        Piece pB = p1.getPieces().get(1);
        Piece pC = p1.getPieces().get(2);

        movePieceToIndex(pA, 25);
        movePieceToIndex(pB, 25);
        movePieceToIndex(pC, 25);

        Map<GroupKey, List<Piece>> map = groupManager.computeGroups(List.of(pA, pB, pC));

        assertEquals(1, map.size());
        assertEquals(3, map.values().iterator().next().size());
    }
}
