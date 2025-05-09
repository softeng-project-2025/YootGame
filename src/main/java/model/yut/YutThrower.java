package model.yut;

import java.util.Random;

public class YutThrower {

    private static final Random random = new Random();

    public static YutResult throwYut() {
        // 4개의 윷 중 0번 가락을 ‘점 찍힌 윷’(백도 후보)라고 가정
        boolean[] fronts = new boolean[4];
        int frontCount = 0;

        for (int i = 0; i < 4; i++) {
            fronts[i] = random.nextBoolean();   // true = 앞면
            if (fronts[i]) frontCount++;
        }

        // 백도 체크: ‘앞면이 1개뿐이고 그게 0번 가락일 때’
        if (frontCount == 1 && fronts[0]) {
            return YutResult.BACK_DO;
        }

        return switch (frontCount) {
            case 0 -> YutResult.MO;
            case 1 -> YutResult.DO;
            case 2 -> YutResult.GAE;
            case 3 -> YutResult.GEOL;
            case 4 -> YutResult.YUT;
            default -> throw new IllegalStateException("Unexpected count: " + frontCount);
        };
    }
}
