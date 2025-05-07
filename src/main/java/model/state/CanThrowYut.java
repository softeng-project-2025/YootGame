package model.state;

import model.dto.MoveResult;
import model.yut.YutResult;

// 던지기만 허용하는 상태
public interface CanThrowYut extends GameState {
    MoveResult handleYutThrowWithResult(YutResult result);
}