package model.yut;

import model.Game;
import model.dto.GameMessage;
import model.dto.MessageType;

import java.util.Queue;

public class YutQueueHandler {
    public static YutResult pollNextYutResult(Queue<YutResult> queue) {
        return queue.isEmpty() ? null : queue.poll();
    }

    public static boolean hasPendingMoves(Queue<YutResult> queue) {
        return !queue.isEmpty();
    }

    public static YutResult dequeueResult(Game game) {
        YutResult result = game.dequeueYutResult();
        if (result == null) {
            game.setLastMessage(new GameMessage("적용할 윷 결과가 없습니다.", MessageType.WARN));
        }
        return result;
    }
}
