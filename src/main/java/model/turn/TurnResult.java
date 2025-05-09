package model.turn;

import model.piece.Piece;
import model.yut.YutResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TurnResult {

    private final List<YutResult> available = new ArrayList<>();
    private final List<AppliedResult> applied = new ArrayList<>();

    public void add(YutResult result) {
        if (result != null) available.add(result);
    }

    public void addAll(List<YutResult> results) {
        if (results != null) available.addAll(results);
    }

    public void apply(YutResult result, Piece piece) {
        if (available.remove(result)) {
            applied.add(new AppliedResult(result, piece));
        }
    }

    public boolean hasPending() {
        return !available.isEmpty();
    }

    public List<YutResult> getAvailable() {
        return Collections.unmodifiableList(available);
    }

    public YutResult getLastResult() {
        return available.get(available.size() - 1);
    }

    public List<AppliedResult> getApplied() {
        return Collections.unmodifiableList(applied);
    }

    public void clear() {
        available.clear();
        applied.clear();
    }

    public record AppliedResult(YutResult result, Piece piece) {
        public AppliedResult {
            Objects.requireNonNull(result);
            Objects.requireNonNull(piece);
        }
    }
}
