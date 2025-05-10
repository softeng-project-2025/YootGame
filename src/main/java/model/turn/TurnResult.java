package model.turn;

import model.piece.Piece;
import model.yut.YutResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// 한 턴 동안의 윷 결과를 관리합니다.
// 사용자에게 어느 결과를 어느 말에 적용할지 선택할 수 있도록 List 기반으로 보관합니다.
public class TurnResult {

    private final List<YutResult> pending = new ArrayList<>();
    private final List<AppliedResult> applied = new ArrayList<>();

    // 윷 결과를 대기 목록에 추가합니다.
    public void add(YutResult result) {
        if (result != null) {
            pending.add(result);
        }
    }
    // 여러 윷 결과를 한 번에 대기 목록에 추가합니다.
    public void addAll(List<YutResult> results) {
        if (results != null) {
            for (YutResult r : results) {
                if (r != null) pending.add(r);
            }
        }
    }

    // 사용자가 선택한 윷 결과를 특정 말에 적용하고 기록으로 남깁니다.
    public void apply(YutResult result, Piece piece) {
        Objects.requireNonNull(result, "YutResult must not be null");
        Objects.requireNonNull(piece, "Piece must not be null");
        if (pending.remove(result)) {
            applied.add(new AppliedResult(result, piece));
        }
    }

    // 아직 적용되지 않은 윷 결과가 남아있는지 확인합니다.
    public boolean hasPending() {
        return !pending.isEmpty();
    }

    // 대기 중인 모든 윷 결과를 반환합니다.
    public List<YutResult> getPending() {
        return Collections.unmodifiableList(pending);
    }

    // 가장 최근에 대기 목록에 추가된 윷 결과를 반환합니다.
    public YutResult getLastResult() {
        if (pending.isEmpty()) throw new IllegalStateException("No pending YutResults");
        return pending.get(pending.size() - 1);
    }

    // 이미 적용된 결과 기록을 반환합니다.
    public List<AppliedResult> getApplied() {
        return Collections.unmodifiableList(applied);
    }

    // 모든 대기 및 기록을 초기화합니다.
    public void clear() {
        pending.clear();
        applied.clear();
    }


    // 윷 결과와 적용된 말을 함께 저장하는 레코드입니다.
    public static record AppliedResult(YutResult result, Piece piece) {
        public AppliedResult {
            Objects.requireNonNull(result);
            Objects.requireNonNull(piece);
        }
    }
}
