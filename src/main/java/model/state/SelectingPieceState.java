package model.state;

import model.Game;
import model.dto.*;
import model.manager.CaptureManager;
import model.manager.GroupManager;
import model.manager.VictoryManager;
import model.piece.PathType;
import model.piece.Piece;
import model.position.Position;
import model.turn.TurnResult;
import model.yut.YutResult;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// CanSelectPiece 상태: 사용자가 선택한 윷 결과를 특정 말에 적용하는 책임을 수행합니다.
public class SelectingPieceState implements CanSelectPiece {
    private final Game game;
    private final CaptureManager captureManager;
    private final GroupManager groupManager;

    public SelectingPieceState(Game game) {
        this.game = game;
        this.captureManager = new CaptureManager();
        this.groupManager = new GroupManager();
    }

    @Override
    public MoveResult handlePieceSelect(Piece piece, YutResult yut) {
        // 1) 유효성 검사
        if (piece.isFinished() || piece.getOwner() != game.getTurnManager().currentPlayer()) {
            return MoveResult.fail(yut, MoveFailType.INVALID_SELECTION);
        }
        TurnResult turnResult = game.getTurnResult();
        List<YutResult> pending = turnResult.getPending();
        if (!pending.contains(yut)) {
            return MoveResult.fail(yut, MoveFailType.NO_RESULT);
        }

        // 2) 이동 전: 같은 칸에서 업힐 말 그룹 미리 계산
        List<Piece> allPieces = game.getPlayers().stream()
                .flatMap(p -> p.getPieces().stream())
                .collect(Collectors.toList());
        GroupManager.GroupKey oldKey =
                new GroupManager.GroupKey(piece.getOwner(), piece.getPosition());
        Map<GroupManager.GroupKey, List<Piece>> groupMap =
                groupManager.computeGroups(allPieces);
        List<Piece> riding =
                groupMap.getOrDefault(oldKey, List.of());

        // 3) 대표 말 이동
        turnResult.apply(yut, piece);                           // 기록만
        Position newPos = game.getBoard().movePiece(piece, yut); // 실제 위치 변경

        // 3-1) 다음 위치에 내 말이 있으면 업힐 말의 path를 따라가도록 변경
        GroupManager.GroupKey newKey =
                new GroupManager.GroupKey(piece.getOwner(), newPos);
        List<Piece> follow =
                groupMap.getOrDefault(newKey, List.of());
        if (!follow.isEmpty()) {
            Piece follower = follow.get(0);
            piece.setPathType(follower.getPathType());
            piece.setCustomPath(follower.getCustomPath());
            piece.setPathIndex(follower.getPathIndex());
        }

        // 4) 그룹원도 함께 이동
        for (Piece p : riding) {
            if (p != piece) {
                game.getBoard().movePiece(p, yut);               // 실제 위치 변경
            }
        }

        // 5) 캡처 처리
        Map<Piece, List<Piece>> captures = captureManager.handleCaptures(
                List.of(piece),
                game.getPlayers(),
                game.getBoard()
        );
        boolean didCapture = captures.containsKey(piece);

        // 6) 승리 검사
        boolean isGameOver = VictoryManager.hasPlayerWon(piece.getOwner());
        if (isGameOver) {
            return MoveResult.gameOver(yut, piece.getOwner())
                    .withNextStateHint(NextStateHint.GAME_ENDED);
        }

        // 7) 다음 상태 힌트 계산
        boolean bonusTurn = false;
        if (didCapture){
            if (yut != YutResult.YUT && yut != YutResult.MO) bonusTurn = true;
        }

        boolean hasMore = game.getTurnResult().hasPending();
        NextStateHint hint = bonusTurn ? NextStateHint.WAITING_FOR_THROW
                            : hasMore  ? NextStateHint.STAY
                                        : NextStateHint.NEXT_TURN;
        System.out.println(hint + "로가자꾸나!!");

        // 7) MoveResult 반환
        return MoveResult.success(
                yut,
                didCapture,
                bonusTurn,
                game.getTurnManager().currentPlayer(),
                game,
                piece,
                hasMore,
                captures,
                groupMap
        );
    }

}


