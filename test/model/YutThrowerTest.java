package model;


import model.yut.YutResult;
import model.yut.YutThrower;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

// 윷을 던지는 모든 시나리오에 관한 테스트 케이스
public class YutThrowerTest {
    /* 테스트 케이스 1: 지정 윷 던지기를 하는 경우.
     * 지정한 윷이 제대로 나오는지
     */

    /* 테스트 케이스 2: 랜덤 윷 던지기를 하는 경우.
     * 결과가 랜덤하게 잘 나오는지
     */
    @Test
    public void testThrowYutReturnsValidResult() {
        for (int i = 0; i < 100; i++) {
            YutResult result = YutThrower.throwYut();
            assertTrue(result == YutResult.DO ||
                            result == YutResult.GAE ||
                            result == YutResult.GEOL ||
                            result == YutResult.YUT ||
                            result == YutResult.MO,
                    "결과가 유효한 YutResult 여야 함");
        }
    }

    /* 테스트 케이스 3: 윷/모 가 나오는 경우.
     * 한 번 더 throwing 기회가 주어지고, 결과들을 player 가 잘 선택할 수 있는지
     * -> 3번 정도 던지는 경우까지 테스트
     */

    /* 테스트 케이스 4: 말 이동 후 상대의 말을 잡은 경우.
     * throwing 기회가 한 번 더 주어지는지
     */

    /* 테스트 케이스 4-2: 말 이동 후 상대의 말을 잡았으며, 아직 이동 선택지가 남아있는 경우.
     * 잡은 후 바로 throwing 을 하고 남은 선택지에 결과를 추가하는지
     */

    /* 테스트 케이스 4-3: 말 이동 후 상대의 말을 잡았으며, 아직 이동 선택지가 남아있지만, 윷/모 로 잡은 경우.
     * throwing 기회를 주지 않는다.
     */

    /* 테스트 케이스 5: 말이 외곽으로 돌아야 하는 모든 경우.
     * 모서리 칸에 도착하지 않는 경우 외곽으로 잘 도는지
     */
}