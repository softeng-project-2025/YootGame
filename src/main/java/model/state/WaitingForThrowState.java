package model.state;

import model.Game;
import model.dto.MoveResult;
import model.dto.NextStateHint;
import model.player.Player;
import model.yut.YutResult;

public class WaitingForThrowState implements CanThrowYut {

    private final Game game;

    public WaitingForThrowState(Game game) {
        this.game = game;
    }

    @Override
    public MoveResult handleYutThrow(YutResult yut) {
        Player current = game.getTurnManager().currentPlayer();
        boolean noneMovedYet = game.noneMovedYet(current);

        // 백도이고, 말이 하나도 움직이지 않았다면 바로 다음 턴으로 스킵
        if (yut == YutResult.BACK_DO && noneMovedYet) {
            return MoveResult.skipped(yut);
        }

        boolean bonusTurn = (yut == YutResult.YUT || yut == YutResult.MO);
        NextStateHint hint = bonusTurn? NextStateHint.WAITING_FOR_THROW : NextStateHint.STAY;
        System.out.println(bonusTurn + "보너스턴인가?");

        // 그 외엔 원래 로직대로 pending 에 추가
        game.getTurnResult().add(yut);
        return MoveResult.success(
                yut,
                false,
                bonusTurn,
                null,
                game
        ).withNextStateHint(hint);
    }

}