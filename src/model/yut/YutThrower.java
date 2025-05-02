package model.yut;

import java.util.Random;

public class YutThrower {

    private static final Random random = new Random();

    public static YutResult throwYut() {
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