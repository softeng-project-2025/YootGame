package model.manager;

import model.piece.Piece;
import model.player.Player;
import model.position.Position;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// GroupManager: 위치 및 소유자 기준으로 말을 그룹화해 반환합니다.
public class GroupManager {
    // 그룹 키: 플레이어(소유자)와 위치 조합
    public record GroupKey(Player owner, Position position) {}

    public static Map<GroupKey, List<Piece>> computeGroups(List<Piece> allPieces) {
        return allPieces.stream()
                .filter(p -> !p.isFinished())    // 완주한 말은 그룹 대상에서 제외
                .collect(Collectors.groupingBy(p -> new GroupKey(p.getOwner(), p.getPosition())));
    }
}
