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
        return game.dequeueYutResult(); // 메시지 설정 제거
    }
}
