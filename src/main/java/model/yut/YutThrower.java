package model.yut;

import java.util.Random;

public class YutThrower {

    private static final Random random = new Random();

    public static YutResult throwYut() {
        // 5개 윷가락 중 하나가 "등"이면 빽도
        int backdoChance = random.nextInt(10); // 10% 확률로 빽도
        if (backdoChance == 0) {
            return YutResult.BACK_DO;
        }

        // 기본 4개 던지기
        int count = 0;
        for (int i = 0; i < 4; i++) {
            int stick = random.nextInt(2); // 0 or 1
            count += stick;
        }

        switch (count) {
            case 0:
                return YutResult.MO;
            case 1:
                return YutResult.DO;
            case 2:
                return YutResult.GAE;
            case 3:
                return YutResult.GEOL;
            case 4:
                return YutResult.YUT;
            default:
                return YutResult.DO; // fallback
        }
    }
}
